package org.apache.ambari.processflow.descriptor;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;

@XmlRootElement(name = "process")
@XmlAccessorType(XmlAccessType.FIELD)
public class Process {
  @XmlTransient
  private Long processId;

  @XmlAttribute(name = "id", required = true)
  private String id;

  @XmlAttribute(name = "name", required = true)
  private String name;

  @XmlElement(name = "condition")
  private String condition;

  @XmlElements(
      {
          @XmlElement(name = "user-task", type = UserTask.class),
          @XmlElement(name = "server-task", type = ServerTask.class),
          @XmlElement(name = "stop-service-task", type = StopServiceTask.class),
          @XmlElement(name = "start-service-task", type = StartServiceTask.class),
          @XmlElement(name = "stop-component-task", type = StopComponentTask.class),
          @XmlElement(name = "start-component-task", type = StartComponentTask.class),
          @XmlElement(name = "install-component-task", type = InstallComponentTask.class),
          @XmlElement(name = "stop-all-services-task", type = StopAllServicesTask.class),
          @XmlElement(name = "start-all-services-task", type = StartAllServicesTask.class)
      }
  )
  private List<? extends Task> tasks;


  public Long getProcessId() {
    return processId;
  }

  public void setProcessId(Long processId) {
    this.processId = processId;
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

  public List<? extends Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<? extends Task> tasks) {
    this.tasks = tasks;
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
        .add("processId", processId)
        .add("id", id)
        .add("name", name)
        .add("condition", condition)
        .add("tasks", tasks)
        .toString();
  }
}
