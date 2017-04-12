package org.apache.ambari.processflow.orm.entities;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.ambari.processflow.descriptor.Parameter;
import org.apache.ambari.processflow.descriptor.ServerTask;

import com.google.gson.Gson;

@Table(name = "pfe_server_task")
@Entity
@PrimaryKeyJoinColumn(name = "task_id", referencedColumnName = "task_id")
public class ServerTaskEntity extends TaskEntity {

  @Basic
  @Column(name = "classname", nullable = false, insertable = true,
      updatable = true, unique = false, length = 255)
  private String classname = null;

  @Basic
  @Lob
  @Column(name = "parameters")
  private String parameters = null;


  public ServerTaskEntity() {
  }

  public ServerTaskEntity(ServerTask taskDescriptor, Integer sequenceNumber, ProcessEntity processEntity) {
    super(taskDescriptor, sequenceNumber, processEntity);

    ServerTask.Implementation implementation = taskDescriptor.getImplementation();
    if (implementation != null) {
      List<Parameter> implParameters = implementation.getParameters();
      classname = implementation.getClassName();

      if (implParameters != null) {
        parameters = new Gson().toJson(implParameters);
      }
    }
  }

  public String getClassname() {
    return classname;
  }

  public void setClassname(String classname) {
    this.classname = classname;
  }

  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }
}
