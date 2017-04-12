package org.apache.ambari.processflow.orm.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.ambari.processflow.orm.RequiresSession;
import org.apache.ambari.processflow.orm.entities.ProcessFlowInstanceEntity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;


@Singleton
public class ProcessFlowInstanceDAO {

  /**
   * JPA entity manager
   */
  @Inject
  Provider<EntityManager> entityManagerProvider;

  /**
   * Make an instance managed and persistent.
   *
   * @param processFlowInstanceEntity entity to persist
   */
  @Transactional
  public void create(ProcessFlowInstanceEntity processFlowInstanceEntity) {
    entityManagerProvider.get().persist(processFlowInstanceEntity);
  }

  /**
   * Merge the state of the given entity into the current persistence context.
   *
   * @param processFlowInstanceEntity entity to merge
   * @return the merged entity
   */
  @Transactional
  public ProcessFlowInstanceEntity merge(ProcessFlowInstanceEntity processFlowInstanceEntity) {
    return entityManagerProvider.get().merge(processFlowInstanceEntity);
  }

  /**
   * Remove the entity instance.
   *
   * @param processFlowInstanceEntity entity to remove
   */
  @Transactional
  public void remove(ProcessFlowInstanceEntity processFlowInstanceEntity) {
    if (processFlowInstanceEntity != null) {
      EntityManager entityManager = entityManagerProvider.get();

      processFlowInstanceEntity = find(processFlowInstanceEntity.getProcessFlowInstanceId());
      if (processFlowInstanceEntity != null) {
        entityManager.remove(processFlowInstanceEntity);
      }
    }
  }

  /**
   * Refresh the state of the instance from the database,
   * overwriting changes made to the entity, if any.
   *
   * @param processFlowInstanceEntity entity to refresh
   */
  @Transactional
  public void refresh(ProcessFlowInstanceEntity processFlowInstanceEntity) {
    entityManagerProvider.get().refresh(processFlowInstanceEntity);
  }


  @RequiresSession
  public ProcessFlowInstanceEntity find(Long id) {
    return entityManagerProvider.get().find(ProcessFlowInstanceEntity.class, id);
  }

  @RequiresSession
  public List<ProcessFlowInstanceEntity> findAll() {
    TypedQuery<ProcessFlowInstanceEntity> query = entityManagerProvider.get()
        .createNamedQuery("ProcessFlowInstanceEntityFindAll", ProcessFlowInstanceEntity.class);
    return query.getResultList();
  }

  public ProcessFlowInstanceEntity findByHash(String hash) {
    TypedQuery<ProcessFlowInstanceEntity> query = entityManagerProvider.get()
        .createNamedQuery("ProcessFlowInstanceEntityFindByHash", ProcessFlowInstanceEntity.class)
        .setParameter("hash", hash);

    try {
      return query.getSingleResult();
    } catch (NoResultException ignored) {
      return null;
    }
  }
}
