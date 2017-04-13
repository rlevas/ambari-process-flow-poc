package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.AmbariHelper;
import org.apache.ambari.server.workflow.FlowContext;
import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;

import groovyx.net.http.HttpResponseException;

public abstract class ServiceServerTask implements ServerTask {
  public static String SERVICE_PARAMETER = "service";

  @Inject
  protected AmbariHelper ambariHelper;

  private final String operation;

  public ServiceServerTask(String operation) {
    this.operation = operation;
  }

  @Override
  public void execute(FlowContext context) throws Exception {
    String service = context.getParameter(SERVICE_PARAMETER);

    if (!StringUtils.isEmpty(service)) {
      System.out.println(String.format("%s %s", operation, service));
      execute(service, context);
    }

  }

  protected abstract void execute(String service, FlowContext context) throws HttpResponseException, InterruptedException;
}
