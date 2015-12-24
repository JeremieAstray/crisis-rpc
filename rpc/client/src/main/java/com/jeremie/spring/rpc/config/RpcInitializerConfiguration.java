package com.jeremie.spring.rpc.config;

import com.jeremie.spring.rpc.cluster.EurekaHelper;
import com.jeremie.spring.rpc.loadBalance.LoadBalance;
import com.jeremie.spring.rpc.proxy.RpcInitializer;
import com.jeremie.spring.rpc.remote.RpcBean;
import com.jeremie.spring.rpc.remote.RpcClient;
import com.jeremie.spring.rpc.remote.http.HttpRpcClient;
import com.jeremie.spring.rpc.remote.mina.MinaRpcBean;
import com.jeremie.spring.rpc.remote.mina.MinaRpcClient;
import com.jeremie.spring.rpc.remote.netty.NettyRpcBean;
import com.jeremie.spring.rpc.remote.netty.NettyRpcClient;
import com.jeremie.spring.rpc.remote.nio.NioRpcBean;
import com.jeremie.spring.rpc.remote.nio.SocketNioRpcClient;
import com.jeremie.spring.rpc.remote.socket.SocketBioRpcClient;
import com.netflix.appinfo.InstanceInfo;
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
    private EurekaHelper eurekaHelper;
    @Autowired
    private RpcConfiguration rpcConfiguration;
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    private RpcInitializer rpcInitializer;

    @PostConstruct
    public void rpcInitializer() {
        rpcInitializer = new RpcInitializer();
        rpcInitializer.setApplicationContext(applicationContext);
        rpcInitializer.setRpcConfiguration(rpcConfiguration);
        List<RpcBean> rpcBeanList = new ArrayList<>();
        Map<String, RpcClient> rpcClientMap = new ConcurrentHashMap<>();
        this.createRpcClient(rpcBeanList, rpcClientMap);
        rpcInitializer.setRpcBeanList(rpcBeanList);
        rpcInitializer.setRpcClientMap(rpcClientMap);
        rpcInitializer.init();
        rpcBeanList.forEach(RpcBean::init);
    }

    @Override
    @PreDestroy
    public void destroy() throws Exception {
        rpcInitializer.destroy();
    }

    private void createRpcClient(List<RpcBean> rpcBeanList, Map<String, RpcClient> rpcClientMap) {
        rpcBeanList.clear();
        rpcClientMap.clear();
        for (ServiceConfig serviceConfig : rpcConfiguration.getServices()) {
            RpcBean rpcBean = null;
            RpcClient rpcClient;
            switch (serviceConfig.getMethod()) {
                case "mina":
                    rpcBean = new MinaRpcBean();
                    rpcClient = this.getMinaRpcClient((MinaRpcBean) rpcBean);
                    break;
                case "http":
                    rpcClient = this.getHttpRpcClient(serviceConfig.getName(), serviceConfig.getLoadBalance());
                    break;
                case "netty":
                    rpcBean = new NettyRpcBean();
                    rpcClient = this.getNettyRpcClient((NettyRpcBean) rpcBean);
                    break;
                case "bio":
                    rpcClient = this.getSocketBioRpcClient(serviceConfig.getName(), serviceConfig.getLoadBalance());
                    break;
                case "nio":
                    rpcBean = new NioRpcBean();
                    rpcClient = this.getSocketNioRpcClient((NioRpcBean) rpcBean);
                    break;
                default:
                    rpcBean = new MinaRpcBean();
                    rpcClient = this.getMinaRpcClient((MinaRpcBean) rpcBean);
                    break;
            }
            if (rpcBean != null) {
                rpcBean.setClientPort(rpcConfiguration.getDefaultNioClientPort());
                rpcBean.setHost(rpcConfiguration.getDefaultIp());
                rpcBean.setPort(rpcConfiguration.getDefaultPort());
                rpcBean.setEurekaHelper(eurekaHelper);
                rpcBean.setAppName(serviceConfig.getName());
                rpcBean.setEurekaHelper(eurekaHelper);
                rpcBean.setLoadBalance(LoadBalance.getLoadBalance(serviceConfig.getLoadBalance()));
                rpcBeanList.add(rpcBean);
            }
            rpcClientMap.put(serviceConfig.getName(), rpcClient);
        }
    }


    private MinaRpcClient getMinaRpcClient(MinaRpcBean minaRpcBean) {
        return new MinaRpcClient().setMinaRpcBean(minaRpcBean);
    }

    private HttpRpcClient getHttpRpcClient(String name, String loadBalance) {
        LoadBalance loadBalanceInstance = LoadBalance.getLoadBalance(loadBalance);
        String host = null;
        InstanceInfo instanceInfo = loadBalanceInstance.select(eurekaHelper.getHosts(name));
        if (instanceInfo != null)
            host = instanceInfo.getIPAddr();
        return new HttpRpcClient()
                .setPort(rpcConfiguration.getDefaultHttpport())
                .setEurekaHelper(eurekaHelper)
                .setAppName(name)
                .setLoadBalance(loadBalanceInstance)
                .setHost(host != null ? host : rpcConfiguration.getDefaultIp());
    }

    private NettyRpcClient getNettyRpcClient(NettyRpcBean nettyRpcBean) {
        return new NettyRpcClient().setNettyRpcBean(nettyRpcBean);
    }

    private SocketBioRpcClient getSocketBioRpcClient(String name, String loadBalance) {
        LoadBalance loadBalanceInstance = LoadBalance.getLoadBalance(loadBalance);
        String host = null;
        InstanceInfo instanceInfo = loadBalanceInstance.select(eurekaHelper.getHosts(name));
        if (instanceInfo != null)
            host = instanceInfo.getIPAddr();
        return new SocketBioRpcClient()
                .setHost(host != null ? host : rpcConfiguration.getDefaultIp())
                .setEurekaHelper(eurekaHelper)
                .setAppName(name)
                .setLoadBalance(loadBalanceInstance)
                .setPort(rpcConfiguration.getDefaultPort());
    }

    private SocketNioRpcClient getSocketNioRpcClient(NioRpcBean nioRpcBean) {
        return new SocketNioRpcClient().setNioRpcBean(nioRpcBean);
    }
}
