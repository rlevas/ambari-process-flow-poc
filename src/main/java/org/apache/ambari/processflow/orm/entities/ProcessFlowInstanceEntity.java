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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.ambari.processflow.ProcessStatus;

@Table(name = "pfe_process_flow_instance")
@Entity
@NamedQueries({
    @NamedQuery(name = "ProcessFlowInstanceEntityFindAll",
        query = "SELECT pfi FROM ProcessFlowInstanceEntity pfi")
})
@TableGenerator(name = "pfe_process_flow_instance_id_generator",
    table = "pfpoc_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
    pkColumnValue = "pfe_process_flow_instance_seq",
    initialValue = 1)
public class ProcessFlowInstanceEntity {
  @Id
  @Column(name = "process_flow_instance_id", nullable = false, insertable = true, updatable = true)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "pfe_process_flow_instance_id_generator")
  private Long processFlowInstanceId;

  @Basic
  @Lob
  @Column(name = "instance_variables")
  private String instanceVariables;

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

  @OneToMany(mappedBy = "processFlowInstance", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
  private List<ProcessInstanceEntity> processes;

  @ManyToOne
  @JoinColumn(name = "process_flow_id", referencedColumnName = "process_flow_id", nullable = false)
  private ProcessFlowEntity processFlow;

  public ProcessFlowInstanceEntity() {
    processFlowInstanceId = null;
    createdTimestamp = null;
    status = null;
    completedTimestamp = null;
    processes = null;
    processFlow = null;
  }

  public ProcessFlowInstanceEntity(ProcessFlowEntity processFlowEntity) {
    processFlowInstanceId = null;
    createdTimestamp = System.currentTimeMillis();
    status = ProcessStatus.PENDING;
    completedTimestamp = null;
    processFlow = processFlowEntity;

    processes = null;

    List<ProcessEntity> processes = processFlowEntity.getProcesses();
    if (processes != null) {
      this.processes = new ArrayList<>();

      for (ProcessEntity process : processes) {
        this.processes.add(new ProcessInstanceEntity(process, this));
      }
    }
  }

  public Long getProcessFlowInstanceId() {
    return processFlowInstanceId;
  }

  public void setProcessFlowInstanceId(Long processFlowInstanceId) {
    this.processFlowInstanceId = processFlowInstanceId;
  }

  public String getInstanceVariables() {
    return instanceVariables;
  }

  public void setInstanceVariables(String instanceVariables) {
    this.instanceVariables = instanceVariables;
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

  public List<ProcessInstanceEntity> getProcesses() {
    return processes;
  }

  public void setProcesses(List<ProcessInstanceEntity> processes) {
    this.processes = processes;
  }

  public ProcessFlowEntity getProcessFlow() {
    return processFlow;
  }

  public void setProcessFlow(ProcessFlowEntity processFlow) {
    this.processFlow = processFlow;
  }
}
