package org.apache.ambari.processflow.orm.dao;

import javax.persistence.EntityManager;

import org.apache.ambari.processflow.orm.RequiresSession;
import org.apache.ambari.processflow.orm.entities.TaskInstanceEntity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;


@Singleton
public class TaskInstanceDAO {

  /**
   * JPA entity manager
   */
  @Inject
  Provider<EntityManager> entityManagerProvider;

  /**
   * Make an instance managed and persistent.
   *
   * @param taskInstanceEntity entity to persist
   */
  @Transactional
  public void create(TaskInstanceEntity taskInstanceEntity) {
    entityManagerProvider.get().persist(taskInstanceEntity);
  }

  /**
   * Merge the state of the given entity into the current persistence context.
   *
   * @param taskInstanceEntity entity to merge
   * @return the merged entity
   */
  @Transactional
  public TaskInstanceEntity merge(TaskInstanceEntity taskInstanceEntity) {
    return entityManagerProvider.get().merge(taskInstanceEntity);
  }

  /**
   * Refresh the state of the instance from the database,
   * overwriting changes made to the entity, if any.
   *
   * @param taskInstanceEntity entity to refresh
   */
  @Transactional
  public void refresh(TaskInstanceEntity taskInstanceEntity) {
    entityManagerProvider.get().refresh(taskInstanceEntity);
  }


  @RequiresSession
  public TaskInstanceEntity find(Long id) {
    return entityManagerProvider.get().find(TaskInstanceEntity.class, id);
  }
}
