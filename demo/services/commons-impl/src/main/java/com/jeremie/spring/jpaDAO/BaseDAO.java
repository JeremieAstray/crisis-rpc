package com.jeremie.spring.jpaDAO;

import com.jeremie.spring.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * @author guanhong 15/9/30 下午4:04.
 */
public interface BaseDAO<T extends BaseEntity> extends BaseRepository<T, Long> {

    T findByIdAndValidTrue(Long id);

    List<T> findAllByValidTrue();

    List<T> findAllByValidTrue(Sort sort);

    List<T> findAllByValidTrue(Iterable<Long> ids);

    Page<T> findAllByValidTrue(Pageable pageable);


}
