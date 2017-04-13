package org.apache.ambari.processflow.descriptor;

import org.apache.ambari.server.workflow.task.StopComponent;

public class StopComponentTask extends ComponentTask {
  StopComponentTask() {
    super(StopComponent.class.getName());
  }
}
