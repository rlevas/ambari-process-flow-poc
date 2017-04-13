package org.apache.ambari.processflow.descriptor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.ambari.server.workflow.task.StopService;

@XmlRootElement(name = "stop-service-task")
@XmlAccessorType(XmlAccessType.FIELD)
public class StopServiceTask extends ServiceTask {

  public StopServiceTask() {
    super(StopService.class.getName());
  }
}
