package org.apache.ambari.processflow;

import static org.eclipse.persistence.config.PersistenceUnitProperties.CREATE_JDBC_DDL_FILE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.CREATE_ONLY;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_BOTH_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION_MODE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DROP_JDBC_DDL_FILE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_DRIVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_PASSWORD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_URL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_USER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.THROW_EXCEPTIONS;

import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.persist.jpa.PFPOCJpaPersistModule;

public class ControllerModule extends AbstractModule {
  private static final String DEFAULT_DERBY_SCHEMA = "ambari";
  private static final String JDBC_IN_MEMORY_URL = String.format(
      "jdbc:h2:mem:%1$s;ALIAS_COLUMN_NAME=TRUE;INIT=CREATE SCHEMA IF NOT EXISTS %1$s\\;SET SCHEMA %1$s;",
      DEFAULT_DERBY_SCHEMA);
  private static final String JDBC_IN_MEMORY_DRIVER = "org.h2.Driver";
  private static final String JDBC_IN_MEMORY_USER = "sa";
  private static final String JDBC_IN_MEMORY_PASSWORD = "";

  protected void configure() {
    install(buildJpaPersistModule());
  }

  private Module buildJpaPersistModule() {
    PFPOCJpaPersistModule PFPOCJpaPersistModule = new PFPOCJpaPersistModule("process_flow_poc");

    Properties persistenceProperties = ControllerModule.getPersistenceProperties();

    persistenceProperties.setProperty(DDL_GENERATION_MODE, DDL_BOTH_GENERATION);
    persistenceProperties.setProperty(CREATE_JDBC_DDL_FILE, "DDL-create.jdbc");
    persistenceProperties.setProperty(DROP_JDBC_DDL_FILE, "DDL-drop.jdbc");

    PFPOCJpaPersistModule.properties(persistenceProperties);
    return PFPOCJpaPersistModule;
  }

  private static Properties getPersistenceProperties() {
    Properties properties = new Properties();
    properties.setProperty(JDBC_URL, JDBC_IN_MEMORY_URL);
    properties.setProperty(JDBC_DRIVER, JDBC_IN_MEMORY_DRIVER);
    properties.setProperty(JDBC_USER, JDBC_IN_MEMORY_USER);
    properties.setProperty(JDBC_PASSWORD, JDBC_IN_MEMORY_PASSWORD);
    properties.setProperty(DDL_GENERATION, CREATE_ONLY);
    properties.setProperty(THROW_EXCEPTIONS, "true");
    return properties;
  }
}
