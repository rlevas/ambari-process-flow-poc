package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.FlowContext;

import groovyx.net.http.HttpResponseException;

public class StopComponent extends ComponentServerTask {
  public StopComponent() {
    super("Stopping");
  }

  @Override
  protected void execute(String service, String component, String[] hostNames, FlowContext context) throws HttpResponseException, InterruptedException {
    ambariHelper.stopComponent(hostNames, component);
  }
}

