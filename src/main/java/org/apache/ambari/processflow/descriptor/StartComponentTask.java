package org.apache.ambari.processflow.descriptor;

import org.apache.ambari.server.workflow.task.StartComponent;

public class StartComponentTask extends ComponentTask {
  StartComponentTask() {
    super(StartComponent.class.getName());
  }
}
