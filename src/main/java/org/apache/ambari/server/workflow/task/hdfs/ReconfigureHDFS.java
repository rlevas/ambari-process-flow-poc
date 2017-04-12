package org.apache.ambari.server.workflow.task.hdfs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.ambari.server.workflow.AmbariHelper;
import org.apache.ambari.server.workflow.FlowContext;
import org.apache.ambari.server.workflow.task.ServerTask;

import com.google.inject.Inject;

public class ReconfigureHDFS implements ServerTask {
  @Inject
  private AmbariHelper ambariHelper;

  @Override
  public void execute(FlowContext context) throws Exception {
    String nameServiceId = context.getVariable("name_service_id");
    String newNameNodeHost = context.getVariable("additional_nn_host");
    String existingNameNodeHostHost = context.getVariable("original_nn_host");
    String journalHosts = context.getVariable("jn_hosts");

    Map<String, String> hdfsSite = ambariHelper.getConfiguration("hdfs-site");

    hdfsSite.put("dfs.nameservices", nameServiceId);
    hdfsSite.put("dfs.internal.nameservices", nameServiceId);
    hdfsSite.put(String.format("dfs.ha.namenodes.%s", nameServiceId), "nn1,nn2");
    hdfsSite.put(String.format("%s.%s.%s", "dfs.namenode.rpc-address", nameServiceId, "nn1"), String.format("%s:%s", existingNameNodeHostHost, "8020"));
    hdfsSite.put(String.format("%s.%s.%s", "dfs.namenode.rpc-address", nameServiceId, "nn2"), String.format("%s:%s", newNameNodeHost, "8020"));
    hdfsSite.put(String.format("%s.%s.%s", "dfs.namenode.http-address", nameServiceId, "nn1"), String.format("%s:%s", existingNameNodeHostHost, "50070"));
    hdfsSite.put(String.format("%s.%s.%s", "dfs.namenode.http-address", nameServiceId, "nn2"), String.format("%s:%s", newNameNodeHost, "50070"));
    hdfsSite.put(String.format("%s.%s.%s", "dfs.namenode.https-address", nameServiceId, "nn1"), String.format("%s:%s", existingNameNodeHostHost, "50470"));
    hdfsSite.put(String.format("%s.%s.%s", "dfs.namenode.https-address", nameServiceId, "nn2"), String.format("%s:%s", newNameNodeHost, "50470"));
    hdfsSite.put(String.format("dfs.client.failover.proxy.provider.%s", nameServiceId), " org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
    hdfsSite.put("dfs.namenode.shared.edits.dir", String.format("qjournal://%s/%s", formatHosts(journalHosts, "8485", ";"), nameServiceId));
    hdfsSite.put("dfs.ha.fencing.methods", "shell(/bin/true)");
    hdfsSite.put("dfs.ha.automatic-failover.enabled", "true");
    hdfsSite.put("dfs.journalnode.edits.dir", context.getVariable("dfs.journalnode.edits.dir"));

    ambariHelper.setConfiguration("hdfs-site", hdfsSite);


    Map<String, String> coreSite = ambariHelper.getConfiguration("core-site");

    coreSite.put("fs.defaultFS", String.format("hdfs://%s", nameServiceId));
    coreSite.put("hadoop.proxyuser.hdfs.hosts", "*");
    coreSite.put("hadoop.proxyuser.root.groups", "*");
    coreSite.put("hadoop.proxyuser.root.hosts", existingNameNodeHostHost);
    coreSite.put("ha.zookeeper.quorum", formatHosts(ambariHelper.getHostsForComponent("ZOOKEEPER_SERVER"), "2181", ","));

    ambariHelper.setConfiguration("core-site", coreSite);


    if (!ambariHelper.getHostsForComponent("HDFS_CLIENT").contains(newNameNodeHost)) {
      ambariHelper.installComponent(newNameNodeHost, "HDFS_CLIENT");
    }
  }

  private String formatHosts(String hosts, String port, String delimiter) {
    return formatHosts(Arrays.asList(hosts.split(",")), port, delimiter);
  }

  private String formatHosts(Collection<String> hosts, String port, String delimiter) {
    StringBuilder builder = new StringBuilder();

    for (String host : hosts) {
      if (builder.length() != 0) {
        builder.append(delimiter);
      }
      builder.append(host.trim());
      builder.append(':');
      builder.append(port);
    }

    return builder.toString();
  }
}
