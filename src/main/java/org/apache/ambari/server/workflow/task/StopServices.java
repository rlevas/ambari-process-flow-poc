package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.AmbariHelper;
import org.apache.ambari.server.workflow.FlowContext;

import com.google.inject.Inject;

public class StopServices implements ServerTask {

  @Inject
  private AmbariHelper ambariHelper;

  public void execute(FlowContext context) throws Exception {
    System.out.println("Stopping Services...");
    ambariHelper.stopAllServices();
  }
}
