package com.jeremie.spring.commons;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @author guanhong 15/9/12 下午4:08.
 */
public class BaseRepositoryFactoryBean<T extends JpaRepository<Object, Serializable>>
        extends JpaRepositoryFactoryBean<T, Object, Serializable> {

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new BaseRepositoryFactory(em);
    }
}
