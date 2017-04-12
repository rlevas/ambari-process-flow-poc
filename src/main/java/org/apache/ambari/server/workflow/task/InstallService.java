package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.FlowContext;
import org.apache.commons.lang.StringUtils;

public class InstallService implements ServerTask {
  public void execute(FlowContext context) throws Exception {
    // Do nothing...

    String serviceName = context.getParameter("service");

    if (!StringUtils.isEmpty(serviceName)) {
      System.out.println("Installing Service... " + serviceName);
    }
  }
}

