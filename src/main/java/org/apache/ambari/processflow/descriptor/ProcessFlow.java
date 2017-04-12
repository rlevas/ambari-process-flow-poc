package org.apache.ambari.processflow.descriptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Objects;

@XmlRootElement(name = "process-flow")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessFlow {
  @XmlTransient
  private Long processFlowId;

  @XmlTransient
  private String hash;

  @XmlAttribute(name = "name", required = true)
  private String name;

  @XmlTransient
  private String service;

  @XmlTransient
  private String component;

  @XmlElement(name = "process")
  private List<Process> processes;

  public Long getProcessFlowId() {
    return processFlowId;
  }

  public void setProcessFlowId(Long processFlowId) {
    this.processFlowId = processFlowId;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getComponent() {
    return component;
  }

  public void setComponent(String component) {
    this.component = component;
  }

  public List<Process> getProcesses() {
    return processes;
  }

  public void setProcesses(List<Process> processes) {
    this.processes = processes;
  }

  public static ProcessFlow getFromResourceStream(String name) throws JAXBException, IOException {
    ProcessFlow processFlow = null;
    ClassLoader cl = ClassLoader.getSystemClassLoader();

    InputStream configStream = cl.getResourceAsStream(name);
    if (configStream != null) {
      try {
        JAXBContext jaxbContext = JAXBContext.newInstance(ProcessFlow.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        processFlow = (ProcessFlow) jaxbUnmarshaller.unmarshal(configStream);
      } finally {
        configStream.close();
      }
    }

    if (processFlow != null) {
      configStream = cl.getResourceAsStream(name);
      if (configStream != null) {
        try {
          processFlow.setHash(DigestUtils.sha256Hex(configStream));
        } finally {
          configStream.close();
        }
      }
    }

    return processFlow;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("processFlowId", processFlowId)
        .add("hash", hash)
        .add("name", name)
        .add("service", service)
        .add("component", component)
        .add("processes", processes)
        .toString();
  }
}
