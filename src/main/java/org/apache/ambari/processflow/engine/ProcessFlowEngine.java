package org.apache.ambari.processflow.engine;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.ambari.processflow.ProcessStatus;
import org.apache.ambari.processflow.descriptor.ProcessFlow;
import org.apache.ambari.processflow.orm.dao.ProcessFlowDAO;
import org.apache.ambari.processflow.orm.dao.ProcessFlowInstanceDAO;
import org.apache.ambari.processflow.orm.dao.ProcessInstanceDAO;
import org.apache.ambari.processflow.orm.dao.TaskInstanceDAO;
import org.apache.ambari.processflow.orm.entities.ProcessFlowEntity;
import org.apache.ambari.processflow.orm.entities.ProcessFlowInstanceEntity;
import org.apache.ambari.processflow.orm.entities.ProcessInstanceEntity;
import org.apache.ambari.processflow.orm.entities.TaskInstanceEntity;
import org.apache.ambari.server.workflow.AmbariHelper;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class ProcessFlowEngine {

  @Inject
  AmbariHelper ambariHelper;

  @Inject
  private ProcessFlowDAO processFlowDAO;

  @Inject
  private ProcessFlowInstanceDAO processFlowInstanceDAO;

  @Inject
  private ProcessInstanceDAO processInstanceDAO;

  @Inject
  private TaskInstanceDAO taskInstanceDAO;

  private ExpressionParser expressionParser = new SpelExpressionParser();

  public boolean installProcessFlow(String name) throws JAXBException, IOException {
    // Read in descriptor file
    ProcessFlow processFlow = ProcessFlow.getFromResourceStream(name);
    if (processFlow == null) {
      return false;
    } else {
      if (processFlowDAO.findByHash(processFlow.getHash()) == null) {
        ProcessFlowEntity processFlowEntity = ProcessFlowEntity.fromProcessFlowDescriptor(processFlow);
        processFlowDAO.create(processFlowEntity);
      }

      return true;
    }
  }

  public Map<String, String> listProcessFlows() {
    Map<String, String> map = new HashMap<>();

    List<ProcessFlowEntity> all = processFlowDAO.findAll();
    if (all != null) {
      for (ProcessFlowEntity entity : all) {
        map.put(entity.getName(), entity.getHash());
      }
    }

    return map;
  }

  public Long startProcessFlow(String processFlowHash) {
    Long instanceId = null;
    ProcessFlowEntity processFlowEntity = processFlowDAO.findByHash(processFlowHash);

    if (processFlowEntity != null) {
      ProcessFlowInstanceEntity instanceEntity = new ProcessFlowInstanceEntity(processFlowEntity);
      instanceEntity.setStatus(ProcessStatus.IN_PROGRESS);

      processFlowInstanceDAO.create(instanceEntity);

      instanceId = instanceEntity.getProcessFlowInstanceId();
    }

    return instanceId;
  }


  public ProcessFlowInstanceEntity getProcessFlow(Long instanceId) {
    return processFlowInstanceDAO.find(instanceId);
  }

  public List<ProcessInstanceEntity> getProcesses(Long instanceId) {
    List<ProcessInstanceEntity> list = new ArrayList<>();
    ProcessFlowInstanceEntity instance = processFlowInstanceDAO.find(instanceId);
    if (instance != null) {
      List<ProcessInstanceEntity> processes = instance.getProcesses();
      if (processes != null) {
        list.addAll(processes);
      }
    }
    return list;
  }

  public ProcessStatus getProcessFlowStatus(Long instanceId) {
    ProcessFlowInstanceEntity instance = processFlowInstanceDAO.find(instanceId);
    if (instance != null) {
      return instance.getStatus();
    }
    return ProcessStatus.ERROR;
  }

  public TaskInstanceEntity getNextTask(Long instanceId) {
    ProcessFlowInstanceEntity instance = processFlowInstanceDAO.find(instanceId);

    if (instance != null) {
      if (instance.getStatus() == ProcessStatus.PENDING) {
        instance.setStatus(ProcessStatus.IN_PROGRESS);
        instance = processFlowInstanceDAO.merge(instance);
      }

      if (instance.getStatus() == ProcessStatus.IN_PROGRESS) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> instanceData = new Gson().fromJson(instance.getInstanceVariables(), type);
        Context context = new Context(instanceData, ambariHelper.getAmbariClient().getServicesMap().keySet());
        EvaluationContext evaluationContext = new StandardEvaluationContext(context);

        List<ProcessInstanceEntity> processes = instance.getProcesses();
        if (processes != null) {
          for (ProcessInstanceEntity process : processes) {
            if (process.getStatus() == ProcessStatus.PENDING) {
              String condition = process.getProcess().getCondition();

              // Innocent until proven guilty...
              process.setStatus(ProcessStatus.IN_PROGRESS);

              // Try to prove guilty...
              if (condition != null) {
                Expression expression = expressionParser.parseExpression(condition);

                if (!expression.getValue(evaluationContext, Boolean.class)) {
                  process.setStatus(ProcessStatus.SKIPPED);
                }
              }
              processInstanceDAO.merge(process);
            }

            if (process.getStatus() == ProcessStatus.IN_PROGRESS) {
              List<TaskInstanceEntity> tasks = process.getTasks();

              if (tasks != null) {
                for (TaskInstanceEntity task : tasks) {
                  if (task.getStatus() == ProcessStatus.PENDING) {
                    String condition = task.getTask().getCondition();

                    // Innocent until proven guilty...
                    task.setStatus(ProcessStatus.IN_PROGRESS);

                    // Try to prove guilty...
                    if (condition != null) {
                      Expression expression = expressionParser.parseExpression(condition);

                      if (!expression.getValue(evaluationContext, Boolean.class)) {
                        task.setStatus(ProcessStatus.SKIPPED);
                      }
                    }

                    taskInstanceDAO.merge(task);
                  }

                  if (task.getStatus() == ProcessStatus.IN_PROGRESS) {
                    return task;
                  }
                }
              }
            }
          }
        }
      }
    }

    return null;
  }

  public void completeTask(TaskInstanceEntity task) {
    completeTask(task, (String) null);
  }

  public void completeTask(TaskInstanceEntity task, Map<String, String> instanceVariables) {
    completeTask(task, (instanceVariables == null) ? null : new Gson().toJson(instanceVariables));
  }

  @Transactional
  public void completeTask(TaskInstanceEntity task, String instanceVariablesJson) {
    task.setStatus(ProcessStatus.COMPLETE);
    taskInstanceDAO.merge(task);

    if (instanceVariablesJson != null) {
      ProcessFlowInstanceEntity processFlowInstance = task.getProcessInstance().getProcessFlowInstance();
      processFlowInstance.setInstanceVariables(instanceVariablesJson);
      processFlowInstanceDAO.merge(processFlowInstance);
    }

    reconcileFlowInstance(task.getProcessInstance().getProcessFlowInstance());
  }

  @Transactional
  private void reconcileFlowInstance(ProcessFlowInstanceEntity instance) {
    if (instance != null) {
      if (instance.getStatus() == ProcessStatus.IN_PROGRESS) {
        List<ProcessInstanceEntity> processes = instance.getProcesses();
        boolean processesError = false;
        boolean processesComplete = true;

        if (processes != null) {
          for (ProcessInstanceEntity process : processes) {
            ProcessStatus processStatus = process.getStatus();
            if (processStatus == ProcessStatus.IN_PROGRESS) {
              List<TaskInstanceEntity> tasks = process.getTasks();
              boolean tasksError = false;
              boolean tasksComplete = true;

              if (tasks != null) {
                for (TaskInstanceEntity task : tasks) {
                  ProcessStatus taskStatus = task.getStatus();
                  tasksComplete &= ((taskStatus == ProcessStatus.COMPLETE) || (taskStatus == ProcessStatus.SKIPPED));
                  tasksError |= (taskStatus == ProcessStatus.ERROR);
                }
              }

              if (tasksError) {
                process.setStatus(ProcessStatus.ERROR);
                processInstanceDAO.merge(process);
              } else if (tasksComplete) {
                process.setStatus(ProcessStatus.COMPLETE);
                processInstanceDAO.merge(process);
              }

            } else if (processStatus == ProcessStatus.SKIPPED) {
              // If the process was skipped, make sure the tasks were marked as skipped as well.
              List<TaskInstanceEntity> tasks = process.getTasks();
              if (tasks != null) {
                for (TaskInstanceEntity task : tasks) {
                  task.setStatus(ProcessStatus.SKIPPED);
                  taskInstanceDAO.merge(task);
                }
              }
            }

            processStatus = process.getStatus();
            processesComplete &= ((processStatus == ProcessStatus.COMPLETE) || (processStatus == ProcessStatus.SKIPPED));
            processesError |= (processStatus == ProcessStatus.ERROR);
          }
        }

        if (processesError) {
          instance.setStatus(ProcessStatus.ERROR);
          processFlowInstanceDAO.merge(instance);
        } else if (processesComplete) {
          instance.setStatus(ProcessStatus.COMPLETE);
          processFlowInstanceDAO.merge(instance);
        }
      }
    }
  }

  public String evaluate(String expression, Map<String, String> instanceVariables) {
    Context context = new Context(instanceVariables, ambariHelper.getAmbariClient().getServicesMap().keySet());
    EvaluationContext evaluationContext = new StandardEvaluationContext(context);
    Expression expr = expressionParser.parseExpression(expression);
    return expr.getValue(evaluationContext, String.class);
  }

  private class Context {
    private final Map<String, String> instanceData;

    private final Set<String> services;

    public Context(Map<String, String> instanceData, Set<String> services) {
      this.instanceData = instanceData;
      this.services = services;
    }

    public Map<String, String> getInstanceData() {
      return instanceData;
    }

    public Set<String> getServices() {
      return services;
    }
  }
}
