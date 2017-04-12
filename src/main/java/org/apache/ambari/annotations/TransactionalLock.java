/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.locks.ReadWriteLock;

import com.google.inject.persist.Transactional;

/**
 * The {@link TransactionalLock} annotation is used to provide advice around a
 * joinpoint which will invoke a lock. The lock is invoked before the method
 * begins and is released after it has executed.
 * <p/>
 * This is mainly used in combination with {@link Transactional} methods to
 * provide locking around the entire transaction in order to prevent other
 * methods from performing work before a transaction has completed.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface TransactionalLock {

  /**
   * The logic unit of work being locked.
   *
   * @return
   */
  LockArea lockArea();

  /**
   * @return
   */
  LockType lockType();

  /**
   * The area that the lock is being applied to. There is exactly 1
   * {@link ReadWriteLock} for every area defined.
   */
  public enum LockArea {
    /**
     * Joinpoint lock around work performed on caching the host role command
     * status in a given stage and request.
     */
    HRC_STATUS_CACHE("server.hrcStatusSummary.cache.enabled");

    /**
     * The property which governs whether the lock area is enabled or disabled.
     * Because of the inherent nature of deadlocks with interceptors that lock,
     * it's wise to be able to disable a lock area dynamically.
     */
    private String m_configurationProperty;

    /**
     * {@code true} if the lock area is enabled and should be lockable.
     */
    private Boolean m_enabled = null;

    /**
     * Constructor.
     *
     * @param configurationProperty
     */
    private LockArea(String configurationProperty) {
      m_configurationProperty = configurationProperty;
    }


    /**
     * Used for testing to clean the internal state of enabled. This should not
     * be used directly.
     */
    void clearEnabled() {
      m_enabled = null;
    }
  }

  /**
   * The type of lock which should be acquired.
   */
  public enum LockType {
    /**
     * Read Lock.
     */
    READ,

    /**
     * Write lock.
     */
    WRITE;
  }
}
