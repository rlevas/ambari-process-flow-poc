package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.FlowContext;

public interface ServerTask {
  void execute(FlowContext context) throws Exception;
}
