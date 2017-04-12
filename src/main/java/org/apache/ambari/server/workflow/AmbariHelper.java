package org.apache.ambari.server.workflow;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ambari.groovy.client.AmbariClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Singleton;

import groovyx.net.http.HttpResponseDecorator;
import groovyx.net.http.HttpResponseException;

@Singleton
public class AmbariHelper {
  private AmbariClient ambariClient = null;

  public void init(String ambariServerHost) {
    ambariClient = new AmbariClient(ambariServerHost);
  }

  public AmbariClient getAmbariClient() {
    return ambariClient;
  }

  public void waitForRequest(int requestId) throws InterruptedException {
    int count = 0;
    long progress = ambariClient.getRequestProgress(requestId).longValue();
    while (progress < 100) {
      System.out.print(".");
      if (++count % 20 == 0) {
        System.out.print(progress + "%");
      }
      if (progress < 0) {
        throw new RuntimeException("Request failed: " + requestId);
      }
      Thread.sleep(1000);
      progress = ambariClient.getRequestProgress(requestId).longValue();
    }
    System.out.println(".");
  }

  public Map<String, Object> hostComponentInfo(final String hostName, final String component) throws IOException, URISyntaxException {
    String text = IOUtils.toString(((java.io.StringReader) ((HttpResponseDecorator) ambariClient.getAmbari().get(
        new HashMap<String, Object>() {{
          put("path", String.format("clusters/%s/hosts/%s/host_components/%s", ambariClient.getClusterName(), hostName, component.toUpperCase()));
        }}
    )).getData()));

    Type type = new TypeToken<Map<String, Object>>() {
    }.getType();

    return new Gson().fromJson(text, type);
  }

  public Set<String> getHostsForComponent(String component) {
    Set<String> hostsForComponent = new HashSet<>();

    if (ambariClient != null) {
      if (!StringUtils.isEmpty(component)) {
        Iterable<String> hosts = ambariClient.getClusterHosts();

        if (hosts != null) {
          for (String host : hosts) {
            Map<String, String> hostComponents = ambariClient.getHostComponentsMap(host);

            if ((hostComponents != null) && hostComponents.containsKey(component)) {
              hostsForComponent.add(host);
            }
          }
        }
      }
    }

    return hostsForComponent;
  }


  public void stopAllServices() throws InterruptedException {
    if (ambariClient != null) {
      waitForRequest(ambariClient.stopAllServices());
    }
  }

  public void stopService(String serviceName) throws InterruptedException {
    if (ambariClient != null) {
      waitForRequest(ambariClient.stopService(serviceName));
    }
  }

  public void startAllServices() throws InterruptedException {
    if (ambariClient != null) {
      waitForRequest(ambariClient.startAllServices());
    }
  }

  public void startService(String serviceName) throws InterruptedException {
    if (ambariClient != null) {
      waitForRequest(ambariClient.startService(serviceName));
    }
  }

  public void installComponent(String hostname, String component) throws InterruptedException, HttpResponseException {
    installComponent(new String[]{hostname}, component);
  }

  public void installComponent(String[] hostNames, String component) throws InterruptedException, HttpResponseException {
    if (ambariClient != null) {
      List<Integer> requestIds = new ArrayList<>(hostNames.length);

      for (String hostName : hostNames) {
        requestIds.add(ambariClient.installComponentsToHost(hostName.trim(), Collections.singletonList(component)).get(component));
      }

      for (Integer requestId : requestIds) {
        waitForRequest(requestId);
      }
    }
  }

  public void deleteComponent(String hostname, String component) throws InterruptedException, HttpResponseException {
    deleteComponent(new String[]{hostname}, component);
  }

  public void deleteComponent(String[] hostNames, String component) throws InterruptedException, HttpResponseException {
    if (ambariClient != null) {
      List<Integer> requestIds = new ArrayList<>(hostNames.length);

      for (String hostName : hostNames) {
        Object result = ambariClient.deleteHostComponents(hostName.trim(), Collections.singletonList(component));
        if (result instanceof Map) {
          requestIds.add((Integer) ((Map) result).get(component));
        }
      }

      for (Integer requestId : requestIds) {
        waitForRequest(requestId);
      }
    }
  }


  public void startComponent(String hostname, String component) throws InterruptedException, HttpResponseException {
    startComponent(new String[]{hostname}, component);
  }

  public void startComponent(String[] hostNames, String component) throws InterruptedException, HttpResponseException {
    if (ambariClient != null) {
      List<Integer> requestIds = new ArrayList<>(hostNames.length);

      for (String hostName : hostNames) {
        requestIds.add(ambariClient.startComponentsOnHost(hostName.trim(), Collections.singletonList(component)).get(component));
      }

      for (Integer requestId : requestIds) {
        waitForRequest(requestId);
      }
    }
  }

  public Map<String, String> getConfiguration(String configType) {

    Map<String, String> configMap = null;

    if (ambariClient != null) {
      Map<String, Map<String, String>> map = ambariClient.getServiceConfigMap(configType);
      if (map != null) {
        configMap = map.get(configType);
      }
    }

    return configMap;
  }

  public Object setConfiguration(String type, Map<String, String> properties) {
    if (ambariClient != null) {
      return ambariClient.modifyConfiguration(type, properties);
    }
    return null;
  }
}
