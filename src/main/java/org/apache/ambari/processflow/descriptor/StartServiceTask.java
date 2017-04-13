package org.apache.ambari.processflow.descriptor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.ambari.server.workflow.task.StartService;

@XmlRootElement(name = "start-service-task")
@XmlAccessorType(XmlAccessType.FIELD)
public class StartServiceTask extends ServiceTask {

  public StartServiceTask() {
    super(StartService.class.getName());
  }
}
