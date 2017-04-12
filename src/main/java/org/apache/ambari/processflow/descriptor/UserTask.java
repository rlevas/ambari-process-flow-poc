package org.apache.ambari.processflow.descriptor;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.ambari.processflow.TaskType;

import com.google.common.base.Objects;

@XmlRootElement(name = "user-task")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserTask extends Task {
  @XmlElement(name = "description")
  private String description = null;

  @XmlElementWrapper(name = "fields")
  @XmlElement(name = "field")
  private List<Field> fields = null;

  public UserTask() {
    super(TaskType.USER);
  }

  public List<Field> getFields() {
    return fields;
  }

  public void setFields(List<Field> fields) {
    this.fields = fields;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("taskId", getTaskId())
        .add("id", getId())
        .add("name", getName())
        .add("description", description)
        .add("fields", fields)
        .toString();
  }

}
