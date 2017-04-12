package org.apache.ambari.server.workflow.task.hdfs;

import java.util.Map;
import java.util.Set;

import org.apache.ambari.server.workflow.AmbariHelper;
import org.apache.ambari.server.workflow.FlowContext;
import org.apache.ambari.server.workflow.task.ServerTask;

import com.google.gson.Gson;
import com.google.inject.Inject;

public class VerifyJNInitialized implements ServerTask {
  @Inject
  private AmbariHelper ambariHelper;

  @Override
  public void execute(FlowContext context) throws Exception {
    System.out.println("Verify JournalNode initialized...");

    String serviceId = context.getVariable("name_service_id");
    String currentNameNodeHost = context.getVariable("original_nn_host");

    String formatted = null;

    do {
      try {
        Map<String, Object> info = ambariHelper.hostComponentInfo(currentNameNodeHost, "JOURNALNODE");
        String status = (String) ((Map) ((Map) ((Map) info.get("metrics")).get("dfs")).get("journalnode")).get("journalsStatus");
        formatted = (String) ((Map) new Gson().fromJson(status, Map.class).get(serviceId)).get("Formatted");
        Thread.sleep(1500);
      } catch (NullPointerException again) {
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
    } while (!"true".equalsIgnoreCase(formatted));

  }
}
