package com.jeremie.spring.jpaDAO;

import com.jeremie.spring.entity.BaseEntity;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;

/**
 * @author guanhong 15/9/12 下午4:00.
 */
public class BaseRepositoryImpl<T extends BaseEntity, ID extends Long>
        extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
    }

    @Override
    public void invalid(Iterable<? extends T> entities) {
        entities.forEach(this::invalid);
    }

    @Override
    public void invalid(T entity) {
        entity.setValid(false);
        super.save(entity);
    }

    @Override
    public void invalid(ID id) {
        this.invalid(super.findOne(id));
    }

    @Override
    public void invalidAll() {
        findAll().forEach(this::invalid);
    }
}
