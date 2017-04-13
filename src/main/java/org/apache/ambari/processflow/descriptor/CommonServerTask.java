package org.apache.ambari.processflow.descriptor;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class CommonServerTask extends ServerTask {
  @XmlTransient()
  private final String implementationClass;

  protected CommonServerTask(String implementationClass) {
    this.implementationClass = implementationClass;
  }

  public String getImplementationClass() {
    return implementationClass;
  }

  @Override
  public Implementation getImplementation() {
    Implementation implementation = new Implementation();
    implementation.setClassName(implementationClass);
    implementation.setParameters(getTaskParameters());
    return implementation;
  }

  protected abstract List<Parameter> getTaskParameters();

  @Override
  public void setImplementation(Implementation implementation) {
    throw new UnsupportedOperationException();
  }

}
