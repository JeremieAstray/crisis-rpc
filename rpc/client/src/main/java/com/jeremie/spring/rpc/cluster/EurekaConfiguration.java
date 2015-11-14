package com.jeremie.spring.rpc.cluster;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guanhong on 2015/11/14 16:22
 */
@Component
public class EurekaConfiguration {
    @Autowired
    private EurekaClient discoverEurekaClient;

    public List<String> getHosts() {
        List<InstanceInfo> instances = discoverEurekaClient.getInstancesByVipAddress("rpc-server", false);
        List<String> hosts = new ArrayList<>();
        instances.stream()
                .filter(instanceInfo -> InstanceInfo.InstanceStatus.UP.equals(instanceInfo.getStatus()))
                .forEach(instanceInfo -> hosts.add(instanceInfo.getIPAddr()));
        return hosts;
    }
}
