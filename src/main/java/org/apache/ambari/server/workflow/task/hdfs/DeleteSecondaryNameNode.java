package org.apache.ambari.server.workflow.task.hdfs;

import org.apache.ambari.server.workflow.FlowContext;
import org.apache.ambari.server.workflow.task.DeleteComponent;
import org.apache.ambari.server.workflow.task.StartComponent;
import org.apache.commons.lang.StringUtils;

public class DeleteSecondaryNameNode extends DeleteComponent {

  @Override
  public void execute(FlowContext context) throws Exception {
    context.setParameter("service", "HDFS");
    context.setParameter("component", "SECONDARY_NAMENODE");
    context.setParameter("hosts", StringUtils.join(ambariHelper.getHostsForComponent("SECONDARY_NAMENODE"), ","));

    super.execute(context);
  }
}
