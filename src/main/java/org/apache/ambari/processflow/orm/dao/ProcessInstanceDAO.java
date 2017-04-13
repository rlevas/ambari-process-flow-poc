package org.apache.ambari.processflow.orm.dao;

import javax.persistence.EntityManager;

import org.apache.ambari.processflow.orm.RequiresSession;
import org.apache.ambari.processflow.orm.entities.ProcessInstanceEntity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;


@Singleton
public class ProcessInstanceDAO {

  /**
   * JPA entity manager
   */
  @Inject
  Provider<EntityManager> entityManagerProvider;

  /**
   * Make an instance managed and persistent.
   *
   * @param processInstanceEntity entity to persist
   */
  @Transactional
  public void create(ProcessInstanceEntity processInstanceEntity) {
    entityManagerProvider.get().persist(processInstanceEntity);
  }

  /**
   * Merge the state of the given entity into the current persistence context.
   *
   * @param processInstanceEntity entity to merge
   * @return the merged entity
   */
  @Transactional
  public ProcessInstanceEntity merge(ProcessInstanceEntity processInstanceEntity) {
    return entityManagerProvider.get().merge(processInstanceEntity);
  }

  /**
   * Refresh the state of the instance from the database,
   * overwriting changes made to the entity, if any.
   *
   * @param processInstanceEntity entity to refresh
   */
  @Transactional
  public void refresh(ProcessInstanceEntity processInstanceEntity) {
    entityManagerProvider.get().refresh(processInstanceEntity);
  }


  @RequiresSession
  public ProcessInstanceEntity find(Long id) {
    return entityManagerProvider.get().find(ProcessInstanceEntity.class, id);
  }
}
