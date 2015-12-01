package com.adol.spring.home.jpaService.Impl;

import com.adol.spring.home.jpaService.AdolService;
import com.jeremie.spring.service.BaseService;
import org.springframework.stereotype.Service;

/**
 * @author guanhong 15/9/10 下午10:11.
 */
@Service("UserService")
public class AdolServiceImpl extends BaseService implements AdolService {

    @Override
    public String getSomethingTest() {
        return "this is adol";
    }
}
