package org.apache.ambari.processflow.descriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.ambari.server.workflow.task.ServiceServerTask;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ServiceTask extends CommonServerTask {

  @XmlElement(name = "service")
  private String service = null;

  ServiceTask(String implementationClass) {
    super(implementationClass);
  }

  @Override
  protected List<Parameter> getTaskParameters() {
    return new ArrayList<>(Collections.singletonList(new Parameter(ServiceServerTask.SERVICE_PARAMETER, service)));
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }
}
