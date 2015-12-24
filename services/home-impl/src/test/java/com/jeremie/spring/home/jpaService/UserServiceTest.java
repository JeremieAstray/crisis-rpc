package com.jeremie.spring.home.jpaService;

import com.jeremie.spring.commons.JpaBaseTest;
import com.jeremie.spring.home.entity.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * @author guanhong 15/9/15 上午11:27.
 */
public class UserServiceTest extends JpaBaseTest {
    @Autowired
    private UserService jpaUserService;

    @Test
    public void testGetUserList() throws Exception {
        List<User> users = jpaUserService.getUserList(0, 10);
        System.out.println(users.get(0).getUsername());
    }

    @Test
    public void testGetById() throws Exception {
        User user = jpaUserService.getById(1l);
        System.out.println(user.getUsername());
    }

    @Test
    public void testFindByUsername() throws Exception {
        User user = jpaUserService.findByUsername("test");
        System.out.println(user.getUsername());
    }

}
