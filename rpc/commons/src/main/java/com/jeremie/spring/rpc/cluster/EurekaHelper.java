package com.jeremie.spring.rpc.cluster;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanhong 15/12/24 下午5:59.
 */
@Configuration
public class EurekaHelper {

    @Autowired
    private EurekaClient discoverEurekaClient;

    public InstanceInfo getHost(String providers) {
        return discoverEurekaClient.getNextServerFromEureka(providers, false);
    }
}
