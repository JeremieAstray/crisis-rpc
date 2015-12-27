package com.jeremie.spring.commons;


import com.jeremie.spring.entity.BaseEntity;
import com.jeremie.spring.jpaDAO.BaseRepositoryImpl;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;

/**
 * @author guanhong 15/9/12 下午4:13.
 */
public class BaseRepositoryFactory extends JpaRepositoryFactory {


    public BaseRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected BaseRepositoryImpl<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        return new BaseRepositoryImpl<>((Class<BaseEntity>) information.getDomainType(), entityManager);
    }


    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return BaseRepositoryImpl.class;
    }
}
