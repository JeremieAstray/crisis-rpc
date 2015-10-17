package com.jeremie.spring.commons;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author guanhong 15/9/21 下午3:50.
 */
public class JpaBaseTest extends BaseTest {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Before
    public void before(){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(entityManager));
    }

    @After
    public void after() {
        EntityManagerHolder holder = (EntityManagerHolder) TransactionSynchronizationManager.getResource(entityManagerFactory);
        EntityManagerFactoryUtils.closeEntityManager(holder.getEntityManager());
        TransactionSynchronizationManager.unbindResource(entityManagerFactory);
    }
}
