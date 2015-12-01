package com.adol.spring.home.jpaService.Impl;

import com.adol.spring.home.jpaService.UserService;
import com.jeremie.spring.service.BaseService;
import org.springframework.stereotype.Service;

/**
 * @author guanhong 15/9/10 下午10:11.
 */
@Service("UserService")
public class UserServiceImpl extends BaseService implements UserService {

    @Override
    public String getSomethingTest() {
        return "this is adol";
    }
}
