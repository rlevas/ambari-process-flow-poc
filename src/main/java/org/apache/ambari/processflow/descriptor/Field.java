package org.apache.ambari.processflow.descriptor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "parameter")
@XmlAccessorType(XmlAccessType.FIELD)
public class Field {
  @XmlAttribute(name = "name")
  private String name = null;

  @XmlAttribute(name = "type")
  private String type = null;

  @XmlValue
  private String defaultValue = null;

  public Field(String name, String type) {
  }

  public Field() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
}
