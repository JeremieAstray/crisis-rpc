package com.jeremie.spring.rpc.cluster;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author guanhong 15/12/24 下午5:59.
 */
@Configuration
public class EurekaHelper {

    @Autowired
    private DiscoveryClient discoverEurekaClient;

    public InstanceInfo getHost(String providers) {
        return discoverEurekaClient.getNextServerFromEureka(providers,false);
    }
}
