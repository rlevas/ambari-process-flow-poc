package org.apache.ambari.processflow.descriptor;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ParameterMapAdapter extends XmlAdapter<Parameter[], Map<String, String>> {
  @Override
  public Map<String, String> unmarshal(Parameter[] v) throws Exception {
    return null;
  }

  @Override
  public Parameter[] marshal(Map<String, String> v) throws Exception {
    return null;
  }
}
