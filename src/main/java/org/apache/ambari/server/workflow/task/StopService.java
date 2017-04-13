package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.AmbariHelper;
import org.apache.ambari.server.workflow.FlowContext;

import com.google.inject.Inject;

import groovyx.net.http.HttpResponseException;

public class StopService extends ServiceServerTask {
  @Inject
  private AmbariHelper ambariHelper;

  StopService() {
    super("Stopping");
  }

  @Override
  protected void execute(String service, FlowContext context) throws HttpResponseException, InterruptedException {
    if (ambariHelper.getAmbariClient().getServicesMap().containsKey(service)) {
      ambariHelper.stopService(service);
    }
  }
}

