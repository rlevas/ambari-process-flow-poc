package org.apache.ambari.processflow.orm;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.ambari.annotations.TransactionalLock;
import org.apache.ambari.annotations.TransactionalLock.LockArea;
import org.apache.ambari.annotations.TransactionalLocks;
import org.eclipse.persistence.exceptions.EclipseLinkException;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;
import com.google.inject.persist.jpa.PFPOCJpaPersistService;

public class LocalTxnInterceptor implements MethodInterceptor {
  /**
   * A list of all of the {@link TransactionalLock}s that this interceptor is
   * responsible for. As a thread moves through the system encountering
   * {@link Transactional} and {@link TransactionalLock} methods, this will keep
   * track of which locks the outer-most interceptor will need to release.
   */
  private static final ThreadLocal<LinkedList<TransactionalLock>> s_transactionalLocks = new ThreadLocal<LinkedList<TransactionalLock>>() {
    /**
     * {@inheritDoc}
     */
    @Override
    protected LinkedList<TransactionalLock> initialValue() {
      return new LinkedList<>();
    }
  };

  /**
   * Used to ensure that methods which rely on the completion of
   * {@link Transactional} can detect when they are able to run.
   *
   * @see TransactionalLock
   */
  @Inject
  private final TransactionalLocks transactionLocks = null;

  @Inject
  private final PFPOCJpaPersistService emProvider = null;

  @Inject
  private final UnitOfWork unitOfWork = null;

  // Tracks if the unit of work was begun implicitly by this transaction.
  private final ThreadLocal<Boolean> didWeStartWork = new ThreadLocal<Boolean>();

  /**
   * {@inheritDoc}
   */
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {

    // Should we start a unit of work?
    if (!emProvider.isWorking()) {
      emProvider.begin();
      didWeStartWork.set(true);
    }

    Transactional transactional = readTransactionMetadata(methodInvocation);
    EntityManager em = emProvider.get();

    // lock the transaction if needed
    lockTransaction(methodInvocation);

    // Allow 'joining' of transactions if there is an enclosing @Transactional method.
    if (em.getTransaction().isActive()) {
      return methodInvocation.proceed();
    }

    try {
      // this is the outer-most transactional, begin a transaction
      final EntityTransaction txn = em.getTransaction();
      txn.begin();

      Object result;
      try {
        result = methodInvocation.proceed();

      } catch (Exception e) {
        // commit transaction only if rollback didn't occur
        if (rollbackIfNecessary(transactional, e, txn)) {
          txn.commit();
        }

        detailedLogForPersistenceError(e);

        // propagate whatever exception is thrown anyway
        throw e;
      } finally {
        // Close the em if necessary (guarded so this code doesn't run unless
        // catch fired).
        if (null != didWeStartWork.get() && !txn.isActive()) {
          didWeStartWork.remove();
          unitOfWork.end();
        }
      }

      // everything was normal so commit the txn (do not move into try block
      // above as it
      // interferes with the advised method's throwing semantics)
      try {
        txn.commit();
      } catch (Exception e) {
        detailedLogForPersistenceError(e);
        throw e;
      } finally {
        // close the em if necessary
        if (null != didWeStartWork.get()) {
          didWeStartWork.remove();
          unitOfWork.end();
        }
      }

      // or return result
      return result;
    } finally {
      // unlock all lock areas for this transaction
      unlockTransaction();
    }
  }

  private void detailedLogForPersistenceError(Exception e) {
    if (e instanceof PersistenceException) {
      PersistenceException rbe = (PersistenceException) e;
      Throwable cause = rbe.getCause();

      if (cause != null && cause instanceof EclipseLinkException) {
        EclipseLinkException de = (EclipseLinkException) cause;
        Throwable internal = de.getInternalException();

        int exIndent = 1;
        if (internal != null && internal instanceof SQLException) {
          SQLException exception = (SQLException) internal;

          while (exception != null) {
            exception = exception.getNextException();
            exIndent++;
          }
        }
      }
    }
  }

  // TODO Cache this method's results.
  private Transactional readTransactionMetadata(MethodInvocation methodInvocation) {
    Transactional transactional;
    Method method = methodInvocation.getMethod();
    Class<?> targetClass = methodInvocation.getThis().getClass();

    transactional = method.getAnnotation(Transactional.class);
    if (null == transactional) {
      // If none on method, try the class.
      transactional = targetClass.getAnnotation(Transactional.class);
    }
    if (null == transactional) {
      // If there is no transactional annotation present, use the default
      transactional = Internal.class.getAnnotation(Transactional.class);
    }

    return transactional;
  }

  /**
   * Returns True if rollback DID NOT HAPPEN (i.e. if commit should continue).
   *
   * @param transactional The metadata annotation of the method
   * @param e             The exception to test for rollback
   * @param txn           A JPA Transaction to issue rollbacks on
   */
  static boolean rollbackIfNecessary(Transactional transactional, Exception e,
                                     EntityTransaction txn) {
    if (txn.getRollbackOnly()) {
      txn.rollback();
      return false;
    }

    boolean commit = true;

    //check rollback clauses
    for (Class<? extends Exception> rollBackOn : transactional.rollbackOn()) {

      //if one matched, try to perform a rollback
      if (rollBackOn.isInstance(e)) {
        commit = false;

        //check ignore clauses (supercedes rollback clause)
        for (Class<? extends Exception> exceptOn : transactional.ignore()) {
          //An exception to the rollback clause was found, DON'T rollback
          // (i.e. commit and throw anyway)
          if (exceptOn.isInstance(e)) {
            commit = true;
            break;
          }
        }

        //rollback only if nothing matched the ignore check
        if (!commit) {
          txn.rollback();
        }
        //otherwise continue to commit

        break;
      }
    }

    return commit;
  }

  /**
   * Locks the {@link LockArea} specified on the {@link TransactionalLock}
   * annotation if it exists. If the annotation does not exist, then no work is
   * done.
   * <p/>
   * If a lock is acquired, then {@link #s_transactionalLocks} is updated with
   * the lock so that the outer-most interceptor can release all locks when the
   * transaction has completed.
   *
   * @param methodInvocation
   */
  private void lockTransaction(MethodInvocation methodInvocation) {
    TransactionalLock annotation = methodInvocation.getMethod().getAnnotation(
        TransactionalLock.class);

    // no work to do if the annotation is not present
    if (null == annotation) {
      return;
    }

    // no need to lock again
    if (s_transactionalLocks.get().contains(annotation)) {
      return;
    }

    // there is a lock area, so acquire the lock
    TransactionalLock.LockArea lockArea = annotation.lockArea();
    TransactionalLock.LockType lockType = annotation.lockType();

    ReadWriteLock rwLock = transactionLocks.getLock(lockArea);
    Lock lock = lockType == TransactionalLock.LockType.READ ? rwLock.readLock() : rwLock.writeLock();

    lock.lock();

    // ensure that we add this lock area, otherwise it will never be released
    // when the outer most transaction is committed
    s_transactionalLocks.get().add(annotation);
  }

  /**
   * Unlocks all {@link LockArea}s associated with this transaction or any of
   * the child transactions which were joined. The order that the locks are
   * released is inverted from the order in which they were acquired.
   */
  private void unlockTransaction() {
    LinkedList<TransactionalLock> annotations = s_transactionalLocks.get();
    if (annotations.isEmpty()) {
      return;
    }

    // iterate through all locks which were encountered during the course of
    // this transaction and release them all now that the transaction is
    // committed; iterate reverse to unlock the most recently locked areas
    Iterator<TransactionalLock> iterator = annotations.descendingIterator();
    while (iterator.hasNext()) {
      TransactionalLock annotation = iterator.next();
      TransactionalLock.LockArea lockArea = annotation.lockArea();
      TransactionalLock.LockType lockType = annotation.lockType();

      ReadWriteLock rwLock = transactionLocks.getLock(lockArea);
      Lock lock = lockType == TransactionalLock.LockType.READ ? rwLock.readLock() : rwLock.writeLock();

      lock.unlock();
      iterator.remove();
    }
  }

  @Transactional
  private static class Internal {
  }
}
