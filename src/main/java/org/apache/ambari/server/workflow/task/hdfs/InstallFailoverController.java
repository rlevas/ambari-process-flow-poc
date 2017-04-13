package org.apache.ambari.server.workflow.task.hdfs;

import org.apache.ambari.server.workflow.FlowContext;
import org.apache.ambari.server.workflow.task.InstallComponent;
import org.apache.commons.lang.StringUtils;

public class InstallFailoverController extends InstallComponent {

  @Override
  public void execute(FlowContext context) throws Exception {
    context.setParameter("service", "HDFS");
    context.setParameter("component", "ZKFC");
    context.setParameter("hosts", StringUtils.join(ambariHelper.getHostsForComponent("NAMENODE"), ","));

    super.execute(context);
  }
}
