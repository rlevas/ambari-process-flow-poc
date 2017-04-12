package org.apache.ambari.server.workflow.task;

import org.apache.ambari.server.workflow.AmbariHelper;
import org.apache.ambari.server.workflow.FlowContext;
import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;

import groovyx.net.http.HttpResponseException;

public abstract class ComponentServerTask implements ServerTask {
  @Inject
  protected AmbariHelper ambariHelper;

  @Override
  public void execute(FlowContext context) throws Exception {
    String service = context.getParameter("service");
    String component = context.getParameter("component");
    String hosts = context.getParameter("hosts");
    String[] hostNames;

    if (StringUtils.isEmpty(hosts) || "*".equals(hosts)) {
      hostNames = ambariHelper.getAmbariClient().getHostNames().keySet().toArray(new String[0]);
    } else {
      hostNames = hosts.split(",");
    }

    if (!StringUtils.isEmpty(service) && !StringUtils.isEmpty(component) && (hostNames.length > 0)) {
      execute(service, component, hostNames, context);
    }

  }

  protected abstract void execute(String service, String component, String[] hostNames, FlowContext context) throws HttpResponseException, InterruptedException;
}
