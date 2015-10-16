package com.jeremie.spring.home.jpaService;


import com.jeremie.spring.home.entity.User;

import java.util.List;

/**
 * @author guanhong 15/8/18 下午4:07.
 */
public interface UserService {

    List<User> getUserList(int page, int size) throws Exception;

    User save(User user) throws Exception;

    User getById(Long id) throws Exception;

    void updateUserById(String username, Long id) throws Exception;

    void deleteUser(Long id) throws Exception;

    User findByUsername(String username) throws Exception;

}
