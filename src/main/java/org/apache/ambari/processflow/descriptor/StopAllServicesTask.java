package org.apache.ambari.processflow.descriptor;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.ambari.server.workflow.task.StopServices;

@XmlRootElement(name = "stop-all-services-task")
@XmlAccessorType(XmlAccessType.FIELD)
public class StopAllServicesTask extends CommonServerTask {

  public StopAllServicesTask() {
    super(StopServices.class.getName());
  }

  @Override
  protected List<Parameter> getTaskParameters() {
    return null;
  }
}
