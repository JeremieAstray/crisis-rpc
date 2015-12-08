package com.jeremie.spring.eureka.server;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guanhong 15/12/8 下午2:16.
 */
@RestController
public class MonitorServer {
    @Autowired
    private EurekaClient discoverEurekaClient;

    @RequestMapping
    public String status(){
        return null;
    }
}
