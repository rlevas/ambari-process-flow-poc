package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.FlowContext;

import groovyx.net.http.HttpResponseException;

public class DeleteComponent extends ComponentServerTask {
  public DeleteComponent() {
    super("Deleting");
  }

  @Override
  protected void execute(String service, String component, String[] hostNames, FlowContext context) throws HttpResponseException, InterruptedException {
    ambariHelper.deleteComponent(hostNames, component);
  }
}

