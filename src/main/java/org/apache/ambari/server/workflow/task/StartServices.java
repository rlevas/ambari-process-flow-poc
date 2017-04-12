package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.AmbariHelper;
import org.apache.ambari.server.workflow.FlowContext;

import com.google.inject.Inject;

public class StartServices implements ServerTask {
  @Inject
  private AmbariHelper ambariHelper;

  public void execute(FlowContext context) throws Exception {
    // Do nothing...
    System.out.println("Starting Services...");
    ambariHelper.startAllServices();

    if ("true".equalsIgnoreCase(context.getParameter("test"))) {
      System.out.println("(not) Testing Services...");
    }
  }
}

