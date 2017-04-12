package org.apache.ambari.server.workflow.task.kerberos;

import org.apache.ambari.server.workflow.FlowContext;
import org.apache.ambari.server.workflow.task.InstallService;

public class InstallKerberosClient extends InstallService {
  public void execute(FlowContext context) throws Exception {
    context.setParameter("serviceName", "KERBEROS_CLIENT");
    super.execute(context);
  }
}
