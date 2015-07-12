package org.wf.dp.dniprorada.base.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;

public class BaseRepositoryImpl<T> extends SimpleJpaRepository<T, Long> implements BaseRepository<T> {
    @Autowired
    private SessionFactory sessionFactory;

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public T findOneExpected(Long id) {
        T one = findOne(id);
        if (one == null) {
            throw new EmptyResultDataAccessException(getDomainClass().getSimpleName() + " with id=" + id + " is not found!", 1);
        }
        return one;
    }
}
