package org.apache.ambari.processflow.orm.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.ambari.processflow.ProcessStatus;

@Table(name = "pfe_task_instance")
@Entity
@TableGenerator(name = "pfe_task_instance_id_generator",
    table = "pfpoc_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
    pkColumnValue = "pfe_task_instance_seq",
    initialValue = 1)
public class TaskInstanceEntity {
  @Id
  @Column(name = "task_instance_id", nullable = false, insertable = true, updatable = true)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "pfe_task_instance_id_generator")
  private Long taskInstanceId = null;

  @Basic
  @Enumerated(value = EnumType.STRING)
  @Column(name = "status", nullable = false, length = 100)
  private ProcessStatus status = null;

  @Basic
  @Column(name = "error_code")
  private Integer errorCode = null;

  @Basic
  @Column(name = "created_timestamp", nullable = false)
  private Long createdTimestamp = null;

  @Basic
  @Column(name = "completed_timestamp")
  private Long completedTimestamp = null;

  @Basic
  @Lob
  @Column(name = "stdin")
  private String stdin = null;

  @Basic
  @Lob
  @Column(name = "stdout")
  private String stout = null;

  @Basic
  @Lob
  @Column(name = "structured_out")
  private String structuredOut = null;

  @ManyToOne
  @JoinColumn(name = "process_flow_instance_process_instance_id", referencedColumnName = "process_flow_instance_process_instance_id", nullable = false)
  private ProcessInstanceEntity processInstance = null;

  @ManyToOne
  @JoinColumn(name = "task_id", referencedColumnName = "task_id", nullable = false)
  private TaskEntity task = null;


  public TaskInstanceEntity() {
  }

  public TaskInstanceEntity(TaskEntity taskEntity, ProcessInstanceEntity processInstance) {
    this.status = ProcessStatus.PENDING;
    this.createdTimestamp = System.currentTimeMillis();
    this.task = taskEntity;
    this.processInstance = processInstance;
  }

  public Long getTaskInstanceId() {
    return taskInstanceId;
  }

  public void setTaskInstanceId(Long taskInstanceId) {
    this.taskInstanceId = taskInstanceId;
  }

  public ProcessStatus getStatus() {
    return status;
  }

  public void setStatus(ProcessStatus status) {
    this.status = status;
  }

  public Integer getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(Integer errorCode) {
    this.errorCode = errorCode;
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

  public String getStdin() {
    return stdin;
  }

  public void setStdin(String stdin) {
    this.stdin = stdin;
  }

  public String getStout() {
    return stout;
  }

  public void setStout(String stout) {
    this.stout = stout;
  }

  public String getStructuredOut() {
    return structuredOut;
  }

  public void setStructuredOut(String structuredOut) {
    this.structuredOut = structuredOut;
  }

  public ProcessInstanceEntity getProcessInstance() {
    return processInstance;
  }

  public void setProcessInstance(ProcessInstanceEntity processInstance) {
    this.processInstance = processInstance;
  }

  public TaskEntity getTask() {
    return task;
  }

  public void setTask(TaskEntity task) {
    this.task = task;
  }
}
