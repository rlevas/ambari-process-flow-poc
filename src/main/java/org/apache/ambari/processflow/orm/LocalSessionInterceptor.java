package org.apache.ambari.processflow.orm;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;
import com.google.inject.persist.jpa.PFPOCJpaPersistService;

public class LocalSessionInterceptor implements MethodInterceptor {

  @Inject
  private final PFPOCJpaPersistService emProvider = null;

  private final ThreadLocal<Boolean> didWeStartWork = new ThreadLocal<Boolean>();

  public Object invoke(MethodInvocation invocation) throws Throwable {
    if (!emProvider.isWorking()) {
      emProvider.begin();
      didWeStartWork.set(true);

      try {
        return invocation.proceed();
      } finally {
        if (null != didWeStartWork.get()) {
          didWeStartWork.remove();
          emProvider.end();
        }
      }

    } else {
      //if session was in progress just proceed without additional checks
      return invocation.proceed();
    }
  }
}
