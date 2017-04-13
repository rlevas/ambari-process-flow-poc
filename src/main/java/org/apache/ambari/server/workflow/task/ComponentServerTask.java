package org.apache.ambari.server.workflow.task;

import static org.apache.ambari.server.workflow.task.ServiceServerTask.SERVICE_PARAMETER;

import org.apache.ambari.server.workflow.AmbariHelper;
import org.apache.ambari.server.workflow.FlowContext;
import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;

import groovyx.net.http.HttpResponseException;

public abstract class ComponentServerTask implements ServerTask {
  public static final String COMPONENT_PARAMETER = "component";
  public static final String HOSTS_PARAMETER = "hosts";

  @Inject
  protected AmbariHelper ambariHelper;

  final private String operation;

  protected ComponentServerTask(String operation) {
    this.operation = operation;
  }

  @Override
  public void execute(FlowContext context) throws Exception {
    String service = context.getParameter(SERVICE_PARAMETER);
    String component = context.getParameter(COMPONENT_PARAMETER);
    String hosts = context.getParameter(HOSTS_PARAMETER);
    String[] hostNames;

    if (StringUtils.isEmpty(hosts) || "*".equals(hosts)) {
      hostNames = ambariHelper.getAmbariClient().getHostNames().keySet().toArray(new String[0]);
    } else {
      hostNames = hosts.split(",");
    }

    if (!StringUtils.isEmpty(service) && !StringUtils.isEmpty(component) && (hostNames.length > 0)) {
      System.out.println(String.format("%s %s/%s on host(s) %s", operation, service, component, StringUtils.join(hostNames, ",")));
      execute(service, component, hostNames, context);
    }

  }

  protected abstract void execute(String service, String component, String[] hostNames, FlowContext context) throws HttpResponseException, InterruptedException;
}
