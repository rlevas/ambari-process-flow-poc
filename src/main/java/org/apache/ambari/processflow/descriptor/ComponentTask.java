package org.apache.ambari.processflow.descriptor;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.ambari.server.workflow.task.ComponentServerTask;
import org.apache.ambari.server.workflow.task.ServiceServerTask;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ComponentTask extends CommonServerTask {

  @XmlElement(name = "service")
  private String service = null;

  @XmlElement(name = "component")
  private String component = null;

  @XmlElement(name = "hosts")
  private String hosts = null;

  ComponentTask(String implementationClass) {
    super(implementationClass);
  }

  @Override
  protected List<Parameter> getTaskParameters() {
    ArrayList<Parameter> parameters = new ArrayList<>();
    parameters.add(new Parameter(ServiceServerTask.SERVICE_PARAMETER, service));
    parameters.add(new Parameter(ComponentServerTask.COMPONENT_PARAMETER, component));
    parameters.add(new Parameter(ComponentServerTask.HOSTS_PARAMETER, hosts));
    return parameters;
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

  public String getHosts() {
    return hosts;
  }

  public void setHosts(String hosts) {
    this.hosts = hosts;
  }
}
