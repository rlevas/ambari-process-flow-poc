package org.apache.ambari.processflow.descriptor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.ambari.processflow.TaskType;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Task {
  private Long taskId;

  @XmlAttribute(name = "id", required = true)
  private String id;

  @XmlAttribute(name = "name", required = true)
  private String name;

  @XmlElement(name = "condition")
  private String condition;

  @XmlTransient
  private TaskType type;


  protected Task(TaskType type) {
    this.type = type;
  }

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("taskId", taskId)
        .add("id", id)
        .add("name", name)
        .add("condition", condition)
        .toString();
  }

  public TaskType getType() {
    return type;
  }
}
