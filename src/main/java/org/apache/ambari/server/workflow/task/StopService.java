package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.AmbariHelper;
import org.apache.ambari.server.workflow.FlowContext;
import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;

public class StopService implements ServerTask {
  @Inject
  private AmbariHelper ambariHelper;

  public void execute(FlowContext context) throws Exception {
    String serviceName = context.getParameter("service");

    if (!StringUtils.isEmpty(serviceName)) {
      System.out.println("Stopping Service... " + serviceName);
      ambariHelper.stopService(serviceName);
    }
  }
}

