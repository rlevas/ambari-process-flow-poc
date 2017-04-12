package org.apache.ambari.server.workflow.task.kerberos;

import org.apache.ambari.server.workflow.FlowContext;
import org.apache.ambari.server.workflow.task.ServerTask;

public class FinalizeOperations implements ServerTask {
  public void execute(FlowContext context) throws Exception {
    // Do nothing...
    System.out.println(this.getClass().getName() + ":" + context.getVariable("kdc_type"));
  }
}
