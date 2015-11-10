package com.jeremie.spring.rpc.commons;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guanhong 15/10/24 下午9:32.
 */
@Component
public class RPCConfiguration {

    @Value("${rpc.default.ip}")
    private String defaultIP;
    @Value("${rpc.default.port}")
    private int defaultPort;
    @Value("${rpc.default.httpport}")
    private int defaultHttpPort;
    @Value("${rpc.default.nio.client.port}")
    private int defaultNioClientPort;
    @Value("${rpc.connection.method}")
    private String connectMethod;
    @Autowired
    private EurekaClient discoverEurekaClient;

    protected String getDefaultIP() {
        return defaultIP;
    }

    protected int getDefaultPort() {
        return defaultPort;
    }

    protected int getDefaultHttpPort() {
        return defaultHttpPort;
    }

    protected int getDefaultNioClientPort() {
        return defaultNioClientPort;
    }

    public String getConnectMethod() {
        return connectMethod;
    }

    protected List<String> getHosts() {
        List<InstanceInfo> instances = discoverEurekaClient.getInstancesByVipAddress("rpc-server", false);
        List<String> hosts = new ArrayList<>();
        instances.forEach(instanceInfo -> hosts.add(instanceInfo.getIPAddr()));
        return hosts;
    }

}
