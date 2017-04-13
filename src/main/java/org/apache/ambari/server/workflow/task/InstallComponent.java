package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.FlowContext;

import groovyx.net.http.HttpResponseException;

public class InstallComponent extends ComponentServerTask {
  public InstallComponent() {
    super("Installing");
  }

  @Override
  protected void execute(String service, String component, String[] hostNames, FlowContext context) throws HttpResponseException, InterruptedException {
    ambariHelper.installComponent(hostNames, component);
  }
}

