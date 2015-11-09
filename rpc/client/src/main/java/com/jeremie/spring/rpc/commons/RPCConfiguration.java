package com.jeremie.spring.rpc.commons;

import com.jeremie.spring.rpc.mina.MinaRPCBean;
import com.jeremie.spring.rpc.netty.NettyRPCBean;
import com.jeremie.spring.rpc.nio.RPCNioBean;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guanhong 15/10/24 下午9:32.
 */
@Configuration
public class RPCConfiguration {

    private static EurekaClient discoverEurekaClient;

    @Autowired
    private void setDiscoverEurekaClient(EurekaClient eurekaClient){
        discoverEurekaClient = eurekaClient;
    }

    public static List<String> getHosts(){
        List<InstanceInfo> instances = discoverEurekaClient.getInstancesByVipAddress("rpc-server", false);
        List<String> hosts = new ArrayList<>();
        instances.forEach(instanceInfo -> hosts.add(instanceInfo.getIPAddr()));
        return hosts;
    }


    @Bean(destroyMethod = "destroy")
    public RPCNioBean rpcNioBean(){
        return new RPCNioBean(getHosts());
    }

    @Bean(destroyMethod = "destroy")
    public MinaRPCBean minaRPCBean(){
        return new MinaRPCBean(getHosts());
    }

    @Bean(destroyMethod = "destroy")
    public NettyRPCBean nettyRPCBean(){
        return new NettyRPCBean(getHosts());
    }
}
