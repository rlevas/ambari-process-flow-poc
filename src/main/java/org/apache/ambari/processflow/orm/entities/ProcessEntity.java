package org.apache.ambari.processflow.orm.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.ambari.processflow.descriptor.Process;
import org.apache.ambari.processflow.descriptor.ServerTask;
import org.apache.ambari.processflow.descriptor.Task;
import org.apache.ambari.processflow.descriptor.UserTask;

@Table(name = "pfe_process")
@Entity
@TableGenerator(name = "pfe_process_id_generator",
    table = "pfpoc_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
    pkColumnValue = "pfe_process_seq",
    initialValue = 1)
public class ProcessEntity {
  @Id
  @Column(name = "process_id", nullable = false, insertable = true, updatable = true)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "pfe_process_id_generator")
  private Long processId = null;

  @Basic
  @Column(name = "name", nullable = false, insertable = true,
      updatable = true, unique = false, length = 100)
  private String name = null;

  @Basic
  @Column(name = "sequence_number", nullable = false)
  private Integer sequenceNumber = null;

  @Basic
  @Lob
  @Column(name = "condition")
  private String condition = null;

  @OneToMany(mappedBy = "process", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
  private List<TaskEntity> tasks = null;

  @ManyToOne
  @JoinColumn(name = "process_flow_id", referencedColumnName = "process_flow_id", nullable = false)
  private ProcessFlowEntity processFlow = null;

  @OneToMany(mappedBy = "process", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<ProcessInstanceEntity> instances = null;


  public ProcessEntity() {
  }

  public ProcessEntity(Process process, Integer sequenceNumber, ProcessFlowEntity processFlowEntity) {
    this.processId = null;
    this.name = process.getName();
    this.sequenceNumber = sequenceNumber;
    this.processFlow = processFlowEntity;
    this.condition = process.getCondition();


    List<? extends Task> tasks = process.getTasks();
    if (tasks != null) {
      int counter = 0;

      this.tasks = new ArrayList<>();

      for (Task task : tasks) {
        TaskEntity taskEntity;

        if (task instanceof ServerTask) {
          taskEntity = new ServerTaskEntity((ServerTask) task, counter++, this);
        } else if (task instanceof UserTask) {
          taskEntity = new UserTaskEntity((UserTask) task, counter++, this);
        } else {
          taskEntity = null;
        }

        if (taskEntity != null) {
          this.tasks.add(taskEntity);
        }
      }
    }
  }

  public Long getProcessId() {
    return processId;
  }

  public void setProcessId(Long processId) {
    this.processId = processId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public ProcessFlowEntity getProcessFlow() {
    return processFlow;
  }

  public void setProcessFlow(ProcessFlowEntity processFlow) {
    this.processFlow = processFlow;
  }

  public List<TaskEntity> getTasks() {
    return tasks;
  }

  public void setTasks(List<TaskEntity> tasks) {
    this.tasks = tasks;
  }

  public List<ProcessInstanceEntity> getInstances() {
    return instances;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }
}
