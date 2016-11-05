package com.jeremie.spring.home.jpaDAO;

import com.jeremie.spring.home.entity.User;

/**
 * @author guanhong 15/8/18 下午4:06.
 */
public interface UserRepository {

    void update(String username, Long id);

    User findByUsername(String username);

    User findByIdAndValidTrue(Long id);

}
