package com.jeremie.spring.rpc.commons;

import com.jeremie.spring.rpc.mina.MinaRPCBean;
import com.jeremie.spring.rpc.netty.NettyRPCBean;
import com.jeremie.spring.rpc.nio.RPCNioBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanhong 15/10/24 下午9:32.
 */
@Configuration
public class RPCConfiguration {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean(destroyMethod = "destroy")
    public RPCNioBean rpcNioBean(){
        return new RPCNioBean();
    }

    @Bean(destroyMethod = "destroy")
    public MinaRPCBean minaRPCBean(){
        return new MinaRPCBean(discoveryClient);
    }

    @Bean(destroyMethod = "destroy")
    public NettyRPCBean nettyRPCBean(){
        return new NettyRPCBean();
    }
}
