package org.apache.ambari.processflow.orm.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.ambari.processflow.TaskType;
import org.apache.ambari.processflow.descriptor.Task;

@Table(name = "pfe_task")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@TableGenerator(name = "pfe_task_id_generator",
    table = "pfpoc_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
    pkColumnValue = "pfe_task_seq",
    initialValue = 1)
public abstract class TaskEntity {
  @Id
  @Column(name = "task_id", nullable = false, insertable = true, updatable = true)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "pfe_task_id_generator")
  private Long taskId = null;

  @Basic
  @Column(name = "name", nullable = false, insertable = true,
      updatable = true, unique = false, length = 100)
  private String name= null;

  @Basic
  @Column(name = "sequence_number", nullable = false)
  private Integer sequenceNumber= null;

  @Basic
  @Enumerated(value = EnumType.STRING)
  @Column(name = "type", nullable = false, length = 100)
  private TaskType type= null;

  @Basic
  @Lob
  @Column(name = "condition")
  private String condition = null;

  @ManyToOne
  @JoinColumn(name = "process_id", referencedColumnName = "process_id", nullable = false)
  private ProcessEntity process= null;

  public TaskEntity() {
  }

  public TaskEntity(Task taskDescriptor, Integer sequenceNumber, ProcessEntity processEntity) {
    this.type = taskDescriptor.getType();
    this.name = taskDescriptor.getName();
    this.sequenceNumber = sequenceNumber;
    this.process = processEntity;
    this.condition = taskDescriptor.getCondition();
  }

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TaskType getType() {
    return type;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public ProcessEntity getProcess() {
    return process;
  }

  public void setProcess(ProcessEntity process) {
    this.process = process;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }
}
