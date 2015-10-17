package com.jeremie.spring.jpaDAO;

import com.jeremie.spring.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author guanhong 15/9/12 下午3:25.
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID extends Long> extends JpaRepository<T, ID> {

    void invalid(Iterable<? extends T> entities);

    void invalid(T entity);

    void invalid(ID id);

    void invalidAll();

}
