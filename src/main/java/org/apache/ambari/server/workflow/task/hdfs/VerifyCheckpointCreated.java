package org.apache.ambari.server.workflow.task.hdfs;

import java.util.Map;

import org.apache.ambari.server.workflow.AmbariHelper;
import org.apache.ambari.server.workflow.FlowContext;
import org.apache.ambari.server.workflow.task.ServerTask;

import com.google.inject.Inject;

public class VerifyCheckpointCreated implements ServerTask {
  @Inject
  private AmbariHelper ambariHelper;

  @Override
  public void execute(FlowContext context) throws Exception {
    System.out.println("Verify Checkpoint Created...");

    String currentNameNodeHost = context.getVariable("original_nn_host");

    String safeMode = null;
    do {
      try {
        Map<String, Object> info = ambariHelper.hostComponentInfo(currentNameNodeHost, "NAMENODE");
        safeMode = (String) ((Map) ((Map) ((Map) info.get("metrics")).get("dfs")).get("namenode")).get("Safemode");
        Thread.sleep(1500);
      } catch (NullPointerException again) {
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
    } while (safeMode == null || safeMode.trim().isEmpty());
  }
}
