package org.wf.dp.dniprorada.base.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository with all common methods
 *
 * Use Long as id type, because all entities in project are supposed to be extended from {@link org.wf.dp.dniprorada.base.model.Entity}
 */
@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, Long> {
    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     * @throws EmptyResultDataAccessException if none found
     */
    T findOneExpected(Long id);
}
