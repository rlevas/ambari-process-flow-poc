package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.FlowContext;

import groovyx.net.http.HttpResponseException;

public class StartComponent extends ComponentServerTask {
  @Override
  protected void execute(String service, String component, String[] hostNames, FlowContext context) throws HttpResponseException, InterruptedException {
    System.out.println("Starting Component... " + service + "/" + component);
    ambariHelper.startComponent(hostNames, component);
  }
}

