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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.ambari.processflow.descriptor.Process;
import org.apache.ambari.processflow.descriptor.ProcessFlow;

@Table(name = "pfe_process_flow")
@Entity
@NamedQueries({
    @NamedQuery(name = "ProcessFlowEntityFindAll",
        query = "SELECT pf FROM ProcessFlowEntity pf"),
    @NamedQuery(name = "ProcessFlowEntityFindByHash",
        query = "SELECT pf FROM ProcessFlowEntity pf where pf.hash=:hash")
})
@TableGenerator(name = "pfe_process_flow_id_generator",
    table = "pfpoc_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value",
    pkColumnValue = "pfe_process_flow_seq",
    initialValue = 1)
public class ProcessFlowEntity {
  @Id
  @Column(name = "process_flow_id", nullable = false, insertable = true, updatable = true)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "pfe_process_flow_id_generator")
  private Long processFlowId;

  @Basic
  @Column(name = "hash", nullable = false, insertable = true,
      updatable = true, unique = true, length = 100)
  private String hash;

  @Basic
  @Column(name = "name", nullable = false, insertable = true,
      updatable = true, unique = false, length = 100)
  private String name;

  @Basic
  @Column(name = "service", nullable = true, insertable = true,
      updatable = true, unique = false, length = 100)
  private String service;

  @Basic
  @Column(name = "component", nullable = true, insertable = true,
      updatable = true, unique = false, length = 100)
  private String component;

  @OneToMany(mappedBy = "processFlow", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
  private List<ProcessEntity> processes;

  @OneToMany(mappedBy = "processFlow", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
  private List<ProcessFlowInstanceEntity> instances;


  public ProcessFlowEntity() {
    hash = null;
    name = null;
    service = null;
    component = null;
    processes = null;
  }

  public ProcessFlowEntity(ProcessFlow processFlow) {
    hash = processFlow.getHash();
    name = processFlow.getName();
    service = processFlow.getService();
    component = processFlow.getComponent();

    List<Process> processes = processFlow.getProcesses();
    if (processes != null) {
      this.processes = new ArrayList<>();

      int counter = 0;
      for (Process process : processes) {
        this.processes.add(new ProcessEntity(process, counter++, this));
      }
    }
  }

  public Long getProcessFlowId() {
    return processFlowId;
  }

  public void setProcessFlowId(Long processFlowId) {
    this.processFlowId = processFlowId;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getComponent() {
    return component;
  }

  public void setComponent(String component) {
    this.component = component;
  }

  public List<ProcessEntity> getProcesses() {
    return processes;
  }

  public void setProcesses(List<ProcessEntity> processes) {
    this.processes = processes;
  }

  public static ProcessFlowEntity fromProcessFlowDescriptor(ProcessFlow processFlow) {
    return new ProcessFlowEntity(processFlow);
  }

  public List<ProcessFlowInstanceEntity> getInstances() {
    return instances;
  }
}
