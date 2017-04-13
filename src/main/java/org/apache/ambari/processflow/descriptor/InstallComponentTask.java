package org.apache.ambari.processflow.descriptor;

import org.apache.ambari.server.workflow.task.InstallComponent;

public class InstallComponentTask extends ComponentTask {
  InstallComponentTask() {
    super(InstallComponent.class.getName());
  }
}
