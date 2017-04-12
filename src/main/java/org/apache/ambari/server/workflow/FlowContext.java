package org.apache.ambari.server.workflow;

import java.util.HashMap;
import java.util.Map;

public class FlowContext {
  private final Map<String, String> variables = new HashMap<>();
  private final Map<String, String> parameters = new HashMap<>();

  public FlowContext(Map<String, String> parameters, Map<String, String> variables) {
    if (parameters != null) {
      this.parameters.putAll(parameters);
    }

    if (variables != null) {
      this.variables.putAll(variables);
    }
  }

  public String getVariable(String name) {
    return variables.get(name);
  }

  public void setVariable(String name, String value) {
    variables.put(name, value);
  }

  public String getParameter(String name) {
    return parameters.get(name);
  }

  public void setParameter(String name, String value) {
    parameters.put(name, value);
  }
}
