package org.apache.ambari.processflow.descriptor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "parameter")
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter {
  @XmlAttribute(name = "name")
  private String name;

  @XmlValue
  private String value;

  public Parameter(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public Parameter() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
