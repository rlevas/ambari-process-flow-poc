package org.apache.ambari.processflow.orm.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.ambari.processflow.orm.RequiresSession;
import org.apache.ambari.processflow.orm.entities.ProcessFlowEntity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;


@Singleton
public class ProcessFlowDAO {

  /**
   * JPA entity manager
   */
  @Inject
  Provider<EntityManager> entityManagerProvider;

  /**
   * Make an instance managed and persistent.
   *
   * @param ProcessFlowEntity entity to persist
   */
  @Transactional
  public void create(ProcessFlowEntity ProcessFlowEntity) {
    entityManagerProvider.get().persist(ProcessFlowEntity);
  }

  /**
   * Merge the state of the given entity into the current persistence context.
   *
   * @param ProcessFlowEntity entity to merge
   * @return the merged entity
   */
  @Transactional
  public ProcessFlowEntity merge(ProcessFlowEntity ProcessFlowEntity) {
    return entityManagerProvider.get().merge(ProcessFlowEntity);
  }

  /**
   * Remove the entity instance.
   *
   * @param processFlowEntity entity to remove
   */
  @Transactional
  public void remove(ProcessFlowEntity processFlowEntity) {
    if (processFlowEntity != null) {
      EntityManager entityManager = entityManagerProvider.get();

      processFlowEntity = find(processFlowEntity.getProcessFlowId());
      if (processFlowEntity != null) {
        entityManager.remove(processFlowEntity);
      }
    }
  }

  /**
   * Refresh the state of the instance from the database,
   * overwriting changes made to the entity, if any.
   *
   * @param processFlowEntity entity to refresh
   */
  @Transactional
  public void refresh(ProcessFlowEntity processFlowEntity) {
    entityManagerProvider.get().refresh(processFlowEntity);
  }


  @RequiresSession
  public ProcessFlowEntity find(Long id) {
    return entityManagerProvider.get().find(ProcessFlowEntity.class, id);
  }

  @RequiresSession
  public List<ProcessFlowEntity> findAll() {
    TypedQuery<ProcessFlowEntity> query = entityManagerProvider.get()
        .createNamedQuery("ProcessFlowEntityFindAll", ProcessFlowEntity.class);
    return query.getResultList();
  }

  public ProcessFlowEntity findByHash(String hash) {
    TypedQuery<ProcessFlowEntity> query = entityManagerProvider.get()
        .createNamedQuery("ProcessFlowEntityFindByHash", ProcessFlowEntity.class)
        .setParameter("hash", hash);

    try {
      return query.getSingleResult();
    } catch (NoResultException ignored) {
      return null;
    }
  }
}
