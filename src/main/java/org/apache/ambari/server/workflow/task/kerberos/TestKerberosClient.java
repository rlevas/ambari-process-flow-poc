package org.apache.ambari.server.workflow.task.kerberos;

import org.apache.ambari.server.workflow.FlowContext;
import org.apache.ambari.server.workflow.task.StartService;

public class TestKerberosClient extends StartService {
  public void execute(FlowContext context) throws Exception {
    // Do nothing...
    context.setParameter("serviceName", "KERBEROS_CLIENT");
    super.execute(context);
  }
}
