package org.apache.ambari.processflow.descriptor;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.ambari.processflow.TaskType;

import com.google.common.base.Objects;

@XmlRootElement(name = "server-task")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerTask extends Task {

  @XmlElement(name = "implementation")
  private Implementation implementation;

  public ServerTask() {
    super(TaskType.SERVER);
  }


  public Implementation getImplementation() {
    return implementation;
  }

  public void setImplementation(Implementation implementation) {
    this.implementation = implementation;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("taskId", getTaskId())
        .add("id", getId())
        .add("name", getName())
        .add("implementation", implementation)
        .toString();
  }

  @XmlRootElement(name = "implementation")
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Implementation {
    @XmlAttribute(name = "class")
    String className;

    @XmlElement(name = "parameter")
    private List<Parameter> parameters;

    public String getClassName() {
      return className;
    }

    public void setClassName(String className) {
      this.className = className;
    }

    public List<Parameter> getParameters() {
      return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
      this.parameters = parameters;
    }

    @Override
    public String toString() {
      return Objects.toStringHelper(this)
          .add("className", className)
          .add("parameters", parameters)
          .toString();
    }
  }
}
