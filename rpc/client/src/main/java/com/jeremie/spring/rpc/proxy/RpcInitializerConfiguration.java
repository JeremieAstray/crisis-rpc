package com.jeremie.spring.rpc.proxy;

import com.jeremie.spring.rpc.config.RpcConfiguration;
import com.jeremie.spring.rpc.proxy.RpcInitializer;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author guanhong 15/12/1 下午3:29.
 */
@Configuration
@AutoConfigureBefore
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class RpcInitializerConfiguration {

    @Autowired
    private RpcConfiguration rpcConfiguration;
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Bean(initMethod = "init")
    @AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
    public RpcInitializer rpcInitializer(){
        RpcInitializer rpcInitializer = new RpcInitializer();
        rpcInitializer.setApplicationContext(applicationContext);
        rpcInitializer.setRpcConfiguration(rpcConfiguration);
        return rpcInitializer;
    }
}
