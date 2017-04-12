package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.FlowContext;

import groovyx.net.http.HttpResponseException;

public class DeleteComponent extends ComponentServerTask {
  @Override
  protected void execute(String service, String component, String[] hostNames, FlowContext context) throws HttpResponseException, InterruptedException {
    System.out.println("Deleting Component... " + service + "/" + component);
    ambariHelper.deleteComponent(hostNames, component);
  }
}

