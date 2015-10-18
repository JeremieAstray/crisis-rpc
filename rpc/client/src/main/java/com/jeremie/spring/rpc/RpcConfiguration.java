package com.jeremie.spring.rpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcConfiguration {
    @Autowired
    private ApplicationContext applicationContext;

    @Bean(name = "rpcInitialization")
    public RpcInitialization rpcInitialization(){
        ConfigurableListableBeanFactory configurableListableBeanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        RpcInitialization rpcInitialization = new RpcInitialization(configurableListableBeanFactory);
        rpcInitialization.rpcInit();
        return rpcInitialization;
    }
}