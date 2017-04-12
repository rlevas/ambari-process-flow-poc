import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.ambari.processflow.ControllerModule;
import org.apache.ambari.processflow.ProcessStatus;
import org.apache.ambari.processflow.TaskType;
import org.apache.ambari.processflow.engine.ProcessFlowEngine;
import org.apache.ambari.processflow.orm.GuiceJpaInitializer;
import org.apache.ambari.processflow.orm.entities.ProcessEntity;
import org.apache.ambari.processflow.orm.entities.ProcessFlowEntity;
import org.apache.ambari.processflow.orm.entities.ProcessFlowInstanceEntity;
import org.apache.ambari.processflow.orm.entities.ProcessInstanceEntity;
import org.apache.ambari.processflow.orm.entities.ServerTaskEntity;
import org.apache.ambari.processflow.orm.entities.TaskEntity;
import org.apache.ambari.processflow.orm.entities.TaskInstanceEntity;
import org.apache.ambari.processflow.orm.entities.UserTaskEntity;
import org.apache.ambari.server.workflow.AmbariHelper;
import org.apache.ambari.server.workflow.FlowContext;
import org.apache.ambari.server.workflow.task.ServerTask;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class App {

  public static void main(String[] args) throws Exception {
    new App().run();
  }

  private void run() throws Exception {
    Injector injector = Guice.createInjector(new ControllerModule());

    injector.getInstance(GuiceJpaInitializer.class);

    System.out.println("Ambari Server Details:");
    injector.getInstance(AmbariHelper.class).init(prompt(new Scanner(System.in), "hostname", "c6401.ambari.apache.org", null));

    ProcessFlowEngine engine = injector.getInstance(ProcessFlowEngine.class);

    // Install process flows:
    if (engine.installProcessFlow("enable_kerberos.xml")) {
      System.out.println("Successfully loaded the Enable Kerberos process flow descriptor");
    } else {
      System.err.println("Failed to load the Enable Kerberos process flow descriptor");
    }

    if (engine.installProcessFlow("enable_nn_ha.xml")) {
      System.out.println("Successfully loaded the Enable NN HA process flow descriptor");
    } else {
      System.err.println("Failed to load the Enable NN HA process flow descriptor");
    }

    Map<String, String> installedProcessFlows = engine.listProcessFlows();
    String processFlowName;
    if ((installedProcessFlows != null) && !installedProcessFlows.isEmpty()) {
      Scanner scanner = new Scanner(System.in);

      do {
        System.out.println();
        System.out.println("Installed process flows:");

        for (Map.Entry<String, String> entry : installedProcessFlows.entrySet()) {
          System.out.println(String.format("\t%s : %s", entry.getKey(), entry.getValue()));
        }

        System.out.println();
        System.out.print("Select a process flow: ");
        processFlowName = scanner.nextLine();
      } while ((processFlowName == null) || !installedProcessFlows.containsKey(processFlowName));


      Long instanceId = engine.startProcessFlow(installedProcessFlows.get(processFlowName));
      System.out.println("New Instance: " + instanceId);

      if (instanceId != null) {
        printProcessFlowSummary(engine.getProcessFlow(instanceId));

        while (engine.getProcessFlowStatus(instanceId) == ProcessStatus.IN_PROGRESS) {
          TaskInstanceEntity task = engine.getNextTask(instanceId);

          System.out.println();
          System.out.println(String.format("%s:%s", task.getProcessInstance().getProcess().getName(), task.getTask().getName()));
          System.out.println(String.format("  Instance Variables: %s", task.getProcessInstance().getProcessFlowInstance().getInstanceVariables()));
          TaskType taskType = task.getTask().getType();

          if (taskType == TaskType.USER) {
            Boolean proceed;
            Map<String, String> userInput = null;

            do {
              Gson gson = new Gson();

              TaskEntity taskEntity = task.getTask();
              Field[] fields = null;
              String description = null;

              if (taskEntity instanceof UserTaskEntity) {
                String fieldsJson = ((UserTaskEntity) taskEntity).getFields();
                description = ((UserTaskEntity) taskEntity).getDescription();

                if (!StringUtils.isEmpty(fieldsJson)) {
                  fields = gson.fromJson(fieldsJson, Field[].class);
                }
              }

              if(!StringUtils.isEmpty(description)) {
                System.out.println();
                System.out.println(description);
                System.out.println();
              }

              if (fields != null) {
                userInput = new HashMap<>();
                String json = task.getProcessInstance().getProcessFlowInstance().getInstanceVariables();
                if (!StringUtils.isEmpty(json)) {
                  Type type = new TypeToken<Map<String, String>>() {
                  }.getType();

                  Map<String, String> instanceVariables = gson.fromJson(json, type);
                  userInput.putAll(instanceVariables);
                }

                for (Field field : fields) {
                  userInput.put(field.getName(), String.valueOf(query(scanner, field.getName(), field.getDefaultValue(), determineType(field.getType()))));
                }
              }

              // Force the user to manually continue, like clicking the "next" button.
              proceed = query(scanner, "Continue", "y", Boolean.class);
            } while (!Boolean.TRUE.equals(proceed));

            engine.completeTask(task, userInput);
          } else if (taskType == TaskType.SERVER) {

            TaskEntity taskEntity = task.getTask();
            if (taskEntity instanceof ServerTaskEntity) {
              Gson gson = new Gson();

              ServerTaskEntity serverTaskEntity = (ServerTaskEntity) taskEntity;
              String classname = serverTaskEntity.getClassname();
              Parameter[] parameters = null;
              String parametersJson = serverTaskEntity.getParameters();

              if (!StringUtils.isEmpty(parametersJson)) {
                parameters = gson.fromJson(parametersJson, Parameter[].class);
              }

              String json = task.getProcessInstance().getProcessFlowInstance().getInstanceVariables();
              if (!StringUtils.isEmpty(json)) {
                Type type = new TypeToken<Map<String, String>>() {
                }.getType();

                Map<String, String> instanceVariables = gson.fromJson(json, type);

                ((ServerTask) injector.getInstance(Class.forName(classname))).execute(new FlowContext(createParameterMap(engine, parameters, instanceVariables), instanceVariables));
                engine.completeTask(task);
              }
            }
          } else {
            engine.completeTask(task);
          }
        }

        printProcessFlowSummary(engine.getProcessFlow(instanceId));
      }
    }
  }

  private Map<String, String> createParameterMap(ProcessFlowEngine engine, Parameter[] parameters, Map<String, String> instanceVariables) {
    Map<String, String> map = new HashMap<>();

    if (parameters != null) {
      for (Parameter parameter : parameters) {
        String value = parameter.getValue();

        if (!StringUtils.isEmpty(value) && value.startsWith("eval:")) {
          value = engine.evaluate(value.substring("eval:".length()), instanceVariables);
        }
        map.put(parameter.getName(), value);
      }
    }

    return map;
  }

  private Class<?> determineType(String type) {
    if ("string".equalsIgnoreCase(type)) {
      return String.class;
    } else {
      return Object.class;
    }
  }

  private void printProcessFlowSummary(ProcessFlowInstanceEntity processFlowInstance) {
    System.out.println();
    System.out.println("------------------");
    if (processFlowInstance != null) {
      ProcessFlowEntity processFlow = processFlowInstance.getProcessFlow();
      System.out.println(String.format("%s: %s", processFlow.getName(), processFlowInstance.getStatus().name()));

      List<ProcessInstanceEntity> processInstances = processFlowInstance.getProcesses();
      System.out.println("Processes: ");
      if (processInstances != null) {
        for (ProcessInstanceEntity processInstance : processInstances) {
          ProcessEntity process = processInstance.getProcess();
          System.out.println(String.format("\t%s: %s", process.getName(), processInstance.getStatus().name()));
          List<TaskInstanceEntity> tasksInstances = processInstance.getTasks();
          if (tasksInstances != null) {
            for (TaskInstanceEntity taskInstance : tasksInstances) {
              TaskEntity task = taskInstance.getTask();
              System.out.println(String.format("\t\t%s: %s", task.getName(), taskInstance.getStatus().name()));
            }
          }
        }
      }
    }
    System.out.println("------------------");
  }

  private <T> T query(Scanner scanner, String name, String defaultValue, Class<T> type) {
    if (String.class == type) {
      return type.cast(prompt(scanner, name, defaultValue, null));
    } else if (Boolean.class == type) {
      String value = null;
      do {
        if (value != null) {
          System.out.println("  ** Please answer y or n.");
        }
        value = prompt(scanner, name, defaultValue, "y/n");
      } while (StringUtils.isEmpty(value) ||
          ((!"y".equalsIgnoreCase(value.substring(0, 1))) && (!"n".equalsIgnoreCase(value.substring(0, 1)))));

      return type.cast(("y".equalsIgnoreCase(value.substring(0, 1))));
    } else {
      return null;
    }
  }

  private String prompt(Scanner scanner, String name, String defaultValue, String expectedValues) {
    System.out.print("  ");
    System.out.print(name);

    if (!StringUtils.isEmpty(expectedValues)) {
      System.out.print(" (");
      System.out.print(expectedValues);
      System.out.print(")");
    }

    if (!StringUtils.isEmpty(defaultValue)) {
      System.out.print(" [");
      System.out.print(defaultValue);
      System.out.print(']');
    } else {
      defaultValue = "";
    }
    System.out.print("? ");

    String value = scanner.nextLine();
    return (value.equals("")) ? defaultValue : value;
  }

  class Parameter {
    String name;
    String value;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  class Field {
    String name;
    String type;
    String defaultValue;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getDefaultValue() {
      return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
    }
  }
}
