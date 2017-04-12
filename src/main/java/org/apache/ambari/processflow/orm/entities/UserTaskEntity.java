package org.apache.ambari.processflow.orm.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.ambari.processflow.descriptor.Field;
import org.apache.ambari.processflow.descriptor.UserTask;

import com.google.gson.Gson;

@Table(name = "pfe_user_task")
@Entity
@PrimaryKeyJoinColumn(name = "task_id", referencedColumnName = "task_id")
public class UserTaskEntity extends TaskEntity {

  @Basic
  @Lob
  @Column(name = "description")
  private String description = null;

  @Basic
  @Lob
  @Column(name = "fields")
  private String fields = null;


  public UserTaskEntity() {
    fields = null;
  }

  public UserTaskEntity(UserTask taskDescriptor, Integer sequenceNumber, ProcessEntity processEntity) {
    super(taskDescriptor, sequenceNumber, processEntity);

    description = taskDescriptor.getDescription();

    Iterable<Field> fieldList = taskDescriptor.getFields();
    if (fieldList != null) {
      fields = new Gson().toJson(fieldList);
    }
  }

  public String getFields() {
    return fields;
  }

  public void setFields(String fields) {
    this.fields = fields;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
