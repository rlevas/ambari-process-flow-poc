package org.apache.ambari.processflow.orm.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.ambari.processflow.ProcessStatus;

@Table(name = "pfe_process_instance")
@Entity
@TableGenerator(name = "pfe_process_instance_id_generator",
    table = "pfpoc_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
    pkColumnValue = "pfe_process_instance_seq",
    initialValue = 1)
public class ProcessInstanceEntity {
  @Id
  @Column(name = "process_flow_instance_process_instance_id", nullable = false, insertable = true, updatable = true)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "pfe_process_instance_id_generator")
  private Long processInstanceId;

  @Basic
  @Enumerated(value = EnumType.STRING)
  @Column(name = "status", nullable = false, length = 100)
  private ProcessStatus status;

  @Basic
  @Column(name = "created_timestamp", nullable = false)
  private Long createdTimestamp;

  @Basic
  @Column(name = "completed_timestamp")
  private Long completedTimestamp;

  @OneToMany(mappedBy = "processInstance", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
  private List<TaskInstanceEntity> tasks;

  @ManyToOne
  @JoinColumn(name = "process_flow_instance_id", referencedColumnName = "process_flow_instance_id", nullable = false)
  private ProcessFlowInstanceEntity processFlowInstance;

  @ManyToOne
  @JoinColumn(name = "process_id", referencedColumnName = "process_id", nullable = false)
  private ProcessEntity process;

  public ProcessInstanceEntity() {
    processInstanceId = null;
    status = null;
    createdTimestamp = null;
    completedTimestamp = null;
    tasks = null;
    processFlowInstance = null;
    process = null;
  }

  public ProcessInstanceEntity(ProcessEntity processEntity, ProcessFlowInstanceEntity processFlowInstanceEntity) {
    processInstanceId = null;
    status = ProcessStatus.PENDING;
    createdTimestamp = System.currentTimeMillis();
    completedTimestamp = null;
    processFlowInstance = processFlowInstanceEntity;
    process = processEntity;

    tasks = null;

    List<? extends TaskEntity> tasks = processEntity.getTasks();
    if (tasks != null) {
      this.tasks = new ArrayList<>();

      for (TaskEntity task : tasks) {
        this.tasks.add(new TaskInstanceEntity(task, this));
      }
    }
  }

  public Long getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(Long processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public ProcessStatus getStatus() {
    return status;
  }

  public void setStatus(ProcessStatus status) {
    this.status = status;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Long createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public Long getCompletedTimestamp() {
    return completedTimestamp;
  }

  public void setCompletedTimestamp(Long completedTimestamp) {
    this.completedTimestamp = completedTimestamp;
  }

  public List<TaskInstanceEntity> getTasks() {
    return tasks;
  }

  public void setTasks(List<TaskInstanceEntity> tasks) {
    this.tasks = tasks;
  }

  public ProcessFlowInstanceEntity getProcessFlowInstance() {
    return processFlowInstance;
  }

  public void setProcessFlowInstance(ProcessFlowInstanceEntity processFlowInstance) {
    this.processFlowInstance = processFlowInstance;
  }

  public ProcessEntity getProcess() {
    return process;
  }
}
