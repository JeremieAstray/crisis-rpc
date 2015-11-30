package com.jeremie.spring.rpc.config;

import com.jeremie.spring.rpc.cluster.EurekaConfiguration;
import com.jeremie.spring.rpc.remote.RpcBean;
import com.jeremie.spring.rpc.remote.mina.MinaRpcBean;
import com.jeremie.spring.rpc.remote.netty.NettyRpcBean;
import com.jeremie.spring.rpc.remote.nio.NioRpcBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanhong on 2015/11/10 16:45
 */
@Configuration
@EnableConfigurationProperties(RpcConfiguration.class)
public class RpcBeanConfiguration {

    @Autowired
    private RpcConfiguration rpcConfiguration;
    @Autowired
    private EurekaConfiguration eurekaConfiguration;

    @Bean(destroyMethod = "destroy")
    public RpcBean rpcBean() {
        RpcBean rpcBean;
        switch (rpcConfiguration.getConnectionMethod()) {
            case "mina":
                rpcBean = new MinaRpcBean();
                break;
            case "netty":
                rpcBean = new NettyRpcBean();
                break;
            case "nio":
                rpcBean = new NioRpcBean();
                break;
            default:
                rpcBean = new MinaRpcBean();
                break;
        }
        rpcBean.setClientPort(rpcConfiguration.getDefaultNioClientPort());
        rpcBean.setHost(rpcConfiguration.getDefaultIp());
        rpcBean.setPort(rpcConfiguration.getDefaultPort());
        rpcBean.setHosts(eurekaConfiguration.getHosts());
        return rpcBean;
    }
}
