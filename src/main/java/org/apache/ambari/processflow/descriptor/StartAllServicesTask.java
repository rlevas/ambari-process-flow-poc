package org.apache.ambari.processflow.descriptor;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.ambari.server.workflow.task.StartServices;

@XmlRootElement(name = "start-all-services-task")
@XmlAccessorType(XmlAccessType.FIELD)
public class StartAllServicesTask extends CommonServerTask {

  public StartAllServicesTask() {
    super(StartServices.class.getName());
  }

  @Override
  protected List<Parameter> getTaskParameters() {
    return null;
  }
}
