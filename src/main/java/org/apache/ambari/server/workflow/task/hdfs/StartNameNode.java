package org.apache.ambari.server.workflow.task.hdfs;

import org.apache.ambari.server.workflow.FlowContext;
import org.apache.ambari.server.workflow.task.StartComponent;
import org.apache.commons.lang.StringUtils;

public class StartNameNode extends StartComponent {

  @Override
  public void execute(FlowContext context) throws Exception {
    context.setParameter("service", "HDFS");
    context.setParameter("component", "NAMENODE");
    context.setParameter("hosts", context.getVariable("original_nn_host"));

    super.execute(context);
  }
}
