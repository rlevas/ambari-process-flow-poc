package com.google.inject.persist.jpa;

import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.internal.util.$Nullable;

public class PFPOCJpaPersistService extends JpaPersistService {

  @Inject
  public PFPOCJpaPersistService(@Jpa String persistenceUnitName, @$Nullable @Jpa Properties persistenceProperties) {
    super(persistenceUnitName, persistenceProperties);
  }


}