package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.FlowContext;

import groovyx.net.http.HttpResponseException;

public class StartService extends ServiceServerTask {

  public StartService() {
    super("Starting");
  }

  @Override
  protected void execute(String service, FlowContext context) throws HttpResponseException, InterruptedException {
    if (ambariHelper.getAmbariClient().getServicesMap().containsKey(service)) {
      ambariHelper.startService(service);
    }

    if ("true".equalsIgnoreCase(context.getParameter("test"))) {
      System.out.println("[not supported] Testing Service... " + service);
    }
  }
}

