package org.apache.ambari.processflow.orm;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

/**
 * This class needs to be instantiated with guice to initialize Guice-persist
 */
public class GuiceJpaInitializer {

  @Inject
  public GuiceJpaInitializer(PersistService service) {
    service.start();
  }

}
