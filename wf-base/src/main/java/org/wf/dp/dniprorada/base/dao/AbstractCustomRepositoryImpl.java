package org.wf.dp.dniprorada.base.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractCustomRepositoryImpl {
    @Autowired
    private SessionFactory sessionFactory;
    @PersistenceContext
    private EntityManager entityManager;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
