package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.FlowContext;

import groovyx.net.http.HttpResponseException;

public class StartComponent extends ComponentServerTask {
  public StartComponent() {
    super("Starting");
  }

  @Override
  protected void execute(String service, String component, String[] hostNames, FlowContext context) throws HttpResponseException, InterruptedException {
    ambariHelper.startComponent(hostNames, component);
  }
}

