package com.jeremie.spring.home.jpaService.Impl;

import com.jeremie.spring.home.entity.User;
import com.jeremie.spring.home.jpaDAO.UserRepository;
import com.jeremie.spring.home.jpaService.UserService;
import com.jeremie.spring.rpc.RpcContext;
import com.jeremie.spring.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author guanhong 15/9/10 下午10:11.
 */
@Service("UserService")
public class UserServiceImpl extends BaseService implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public String[] testStringArray() {
        String[] test = new String[]{"test1", "test2", "test3", "test4", "test5"};
        return test;
    }

    @Override
    public long[] testlongArray() {
        long[] longs = new long[]{1L, 2L, 3L, 4L};
        return longs;
    }

    @Override
    public long testLong() {
        long a = 100L;
        return a;
    }

    @Override
    public List<User> getUserList(int page, int size) throws Exception {
        Pageable pageable = new PageRequest(page, size);
        return userRepository.findAll(pageable).getContent();
    }

    @Override
    public User save(User user) throws Exception {
        return userRepository.save(user);
    }

    @Override
    public User getById(long id) throws Exception {
        log.info("------------------>" + RpcContext.getContext().getLocalAddress());
        log.info("------------------>" + RpcContext.getContext().getRemoteAddress());
        return userRepository.findByIdAndValidTrue(id);
    }

    //@Transactional(readOnly = false)
    @Override
    public void updateUserById(String username, Long id) throws Exception {
        userRepository.update(username, id);
    }

    @Override
    public void deleteUser(Long id) throws Exception {
        userRepository.invalid(id);
    }

    @Override
    public User findByUsername(String username) throws Exception {
        return userRepository.findByUsername(username);
    }

    @Override
    public String testGetString() throws Exception {
        return "test";
    }
}
