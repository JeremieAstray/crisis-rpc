package com.jeremie.spring.rpc.config;

import com.jeremie.spring.rpc.cluster.EurekaConfiguration;
import com.jeremie.spring.rpc.protocol.mina.MinaRPCBean;
import com.jeremie.spring.rpc.protocol.netty.NettyRPCBean;
import com.jeremie.spring.rpc.protocol.nio.NioRPCBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanhong on 2015/11/10 16:45
 */
@Configuration
@EnableConfigurationProperties(RPCConfiguration.class)
public class RPCBeanConfiguration {

    @Autowired
    private RPCConfiguration rpcConfiguration;
    @Autowired
    private EurekaConfiguration eurekaConfiguration;

    @Bean(destroyMethod = "destroy")
    public RPCBean rpcBean() {
        RPCBean rpcBean;
        switch (rpcConfiguration.getConnectionMethod()) {
            case "mina":
                rpcBean = new MinaRPCBean();
                break;
            case "netty":
                rpcBean = new NettyRPCBean();
                break;
            case "nio":
                rpcBean = new NioRPCBean();
                break;
            default:
                rpcBean = new MinaRPCBean();
                break;
        }
        rpcBean.setClientPort(rpcConfiguration.getDefaultNioClientPort());
        rpcBean.setHost(rpcConfiguration.getDefaultIp());
        rpcBean.setPort(rpcConfiguration.getDefaultPort());
        rpcBean.setHosts(eurekaConfiguration.getHosts());
        return rpcBean;
    }
}
