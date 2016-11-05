package com.jeremie.spring.rpc.client.config;

import com.jeremie.spring.rpc.client.proxy.RpcInitializer;
import com.jeremie.spring.rpc.remote.RpcBean;
import com.jeremie.spring.rpc.remote.RpcClient;
import com.jeremie.spring.rpc.remote.config.RpcConfiguration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guanhong 15/12/1 下午3:29.
 */
@Configuration
public class RpcInitializerConfiguration implements DisposableBean {

    @Autowired
    private RpcConfiguration rpcConfiguration;
    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @Autowired
    private RpcClient rpcClient;

    private RpcInitializer rpcInitializer;

    @PostConstruct
    public void rpcInitializer() {
        rpcInitializer = new RpcInitializer();
        rpcInitializer.setApplicationContext(applicationContext);
        rpcInitializer.setRpcConfiguration(rpcConfiguration);
        List<RpcBean> rpcBeanList = new ArrayList<>();
        Map<String, RpcClient> rpcClientMap = new ConcurrentHashMap<>();
        rpcInitializer.setRpcBeanList(rpcBeanList);
        rpcInitializer.setRpcClientMap(rpcClientMap);
        rpcInitializer.init();
    }

    @Override
    @PreDestroy
    public void destroy() throws Exception {
        rpcInitializer.destroy();
    }
}
