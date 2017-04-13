package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.FlowContext;

import groovyx.net.http.HttpResponseException;

public class InstallService extends ServiceServerTask {
  public InstallService() {
    super("Installing");
  }

  @Override
  protected void execute(String service, FlowContext context) throws HttpResponseException, InterruptedException {
    // TODO....
  }
}

