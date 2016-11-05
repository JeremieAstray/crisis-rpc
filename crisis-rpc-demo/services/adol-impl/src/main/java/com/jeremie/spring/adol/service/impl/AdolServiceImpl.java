package com.jeremie.spring.adol.service.impl;

import com.jeremie.spring.adol.service.AdolService;
import com.jeremie.spring.service.BaseService;
import org.springframework.stereotype.Service;

/**
 * @author guanhong 15/9/10 下午10:11.
 */
@Service("AdolService")
public class AdolServiceImpl extends BaseService implements AdolService {

    @Override
    public String getSomethingTest() {
        return "this is adol";
    }
}
