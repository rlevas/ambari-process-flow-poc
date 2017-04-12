package com.google.inject.persist.jpa;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.ambari.processflow.orm.LocalSessionInterceptor;
import org.apache.ambari.processflow.orm.LocalTxnInterceptor;
import org.apache.ambari.processflow.orm.RequiresSession;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistModule;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;
import com.google.inject.persist.finder.DynamicFinder;
import com.google.inject.persist.finder.Finder;
import com.google.inject.util.Providers;

public class PFPOCJpaPersistModule extends PersistModule {
  private final String jpaUnit;

  public PFPOCJpaPersistModule(String jpaUnit) {
    if (null != jpaUnit && jpaUnit.length() > 0) {
      this.jpaUnit = jpaUnit;
    } else {
      throw new IllegalArgumentException("jpaUnit should not be null");
    }
  }

  private Properties properties;
  private MethodInterceptor transactionInterceptor;


  @Override
  protected MethodInterceptor getTransactionInterceptor() {
    return transactionInterceptor;
  }

  /**
   * Configures the JPA persistence provider with a set of properties.
   *
   * @param properties A set of name value pairs that configure a JPA persistence
   *                   provider as per the specification.
   */
  public PFPOCJpaPersistModule properties(Properties properties) {
    this.properties = properties;
    return this;
  }

  private final List<Class<?>> dynamicFinders = Lists.newArrayList();

  /**
   * Adds an interface to this module to use as a dynamic finder.
   *
   * @param iface Any interface type whose methods are all dynamic finders.
   */
  public <T> PFPOCJpaPersistModule addFinder(Class<T> iface) {
    dynamicFinders.add(iface);
    return this;
  }

  private <T> void bindFinder(Class<T> iface) {
    if (!isDynamicFinderValid(iface)) {
      return;
    }

    InvocationHandler finderInvoker = new InvocationHandler() {
      @Inject
      JpaFinderProxy finderProxy;

      public Object invoke(final Object thisObject, final Method method, final Object[] args)
          throws Throwable {

        // Don't intercept non-finder methods like equals and hashcode.
        if (!method.isAnnotationPresent(Finder.class)) {
          // This is not ideal, we are using the invocation handler's equals
          // and hashcode as a proxy (!) for the proxy's equals and hashcode.
          return method.invoke(this, args);
        }

        return finderProxy.invoke(new MethodInvocation() {
          public Method getMethod() {
            return method;
          }

          public Object[] getArguments() {
            return null == args ? new Object[0] : args;
          }

          public Object proceed() throws Throwable {
            return method.invoke(thisObject, args);
          }

          public Object getThis() {
            throw new UnsupportedOperationException("Bottomless proxies don't expose a this.");
          }

          public AccessibleObject getStaticPart() {
            throw new UnsupportedOperationException();
          }
        });
      }
    };
    requestInjection(finderInvoker);

    @SuppressWarnings("unchecked") // Proxy must produce instance of type given.
        T proxy = (T) Proxy
        .newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{iface},
            finderInvoker);

    bind(iface).toInstance(proxy);
  }

  private boolean isDynamicFinderValid(Class<?> iface) {
    boolean valid = true;
    if (!iface.isInterface()) {
      addError(iface + " is not an interface. Dynamic Finders must be interfaces.");
      valid = false;
    }

    for (Method method : iface.getMethods()) {
      DynamicFinder finder = DynamicFinder.from(method);
      if (null == finder) {
        addError("Dynamic Finder methods must be annotated with @Finder, but " + iface
            + "." + method.getName() + " was not");
        valid = false;
      }
    }
    return valid;
  }

  @Override
  protected void configurePersistence() {
    bindConstant().annotatedWith(Jpa.class).to(jpaUnit);

    if (null != properties) {
      bind(Properties.class).annotatedWith(Jpa.class).toInstance(properties);
    } else {
      bind(Properties.class).annotatedWith(Jpa.class).toProvider(Providers.<Properties>of(null));
    }

    bind(PFPOCJpaPersistService.class).in(Singleton.class);

    bind(PersistService.class).to(PFPOCJpaPersistService.class);
    bind(UnitOfWork.class).to(PFPOCJpaPersistService.class);
    bind(EntityManager.class).toProvider(PFPOCJpaPersistService.class);
    bind(EntityManagerFactory.class).toProvider(JpaPersistService.EntityManagerFactoryProvider.class);

    transactionInterceptor = new LocalTxnInterceptor();
    requestInjection(transactionInterceptor);

    MethodInterceptor sessionInterceptor = new LocalSessionInterceptor();
    requestInjection(sessionInterceptor);

    // Bind dynamic finders.
    for (Class<?> finder : dynamicFinders) {
      bindFinder(finder);
    }

    bindInterceptor(annotatedWith(RequiresSession.class), any(), sessionInterceptor);
    bindInterceptor(any(), annotatedWith(RequiresSession.class), sessionInterceptor);
  }
}
