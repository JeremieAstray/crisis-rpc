package com.jeremie.spring.home.dao;

import com.jeremie.spring.home.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @author guanhong 15/8/18 下午4:06.
 */
@Repository("HomeUserRepository")
public class UserRepository {

    public void update(String username, Long id) {
        return;
    }

    public User findByUsername(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(username);
        return user;
    }

    public User findByIdAndValidTrue(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("test");
        user.setPassword("test");
        return user;
    }

}
