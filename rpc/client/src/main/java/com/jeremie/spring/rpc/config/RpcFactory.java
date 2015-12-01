package com.jeremie.spring.rpc.config;


import com.jeremie.spring.rpc.cluster.EurekaConfiguration;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author guanhong 15/10/24 上午11:42.
 */
@Configuration
public class RpcFactory {
    @Autowired
    private RpcConfiguration rpcConfiguration;
    @Autowired
    private EurekaConfiguration eurekaConfiguration;
    @Autowired
    private RpcInitializer rpcInitializer;

    @Bean(name = "rpcClientList")
    public List<RpcClient> getRpcClient() {
        Map<String, RpcClient> rpcClientMap = new ConcurrentHashMap<>();
        List<RpcBean> rpcBeanList = new ArrayList<>();
        for (ServiceConfig serviceConfig : rpcConfiguration.getServices()) {
            RpcBean rpcBean = null;
            RpcClient rpcClient;
            switch (serviceConfig.getMethod()) {
                case "mina":
                    rpcBean = new MinaRpcBean();
                    rpcClient = this.getMinaRpcClient((MinaRpcBean) rpcBean);
                    break;
                case "http":
                    rpcClient = this.getHttpRpcClient(serviceConfig.getName());
                    break;
                case "netty":
                    rpcBean = new NettyRpcBean();
                    rpcClient = this.getNettyRpcClient((NettyRpcBean) rpcBean);
                    break;
                case "bio":
                    rpcClient = this.getSocketBioRpcClient(serviceConfig.getName());
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
                rpcBean.setHosts(eurekaConfiguration.getHosts(serviceConfig.getName()));
            }
            rpcBeanList.add(rpcBean);
            rpcClientMap.put(serviceConfig.getName(), rpcClient);
        }
        rpcInitializer.setRpcBeanList(rpcBeanList);
        rpcInitializer.setRpcClientMap(rpcClientMap);
        return rpcClientMap.values().stream().collect(Collectors.toList());
    }

    private MinaRpcClient getMinaRpcClient(MinaRpcBean minaRpcBean) {
        return new MinaRpcClient().setMinaRpcBean(minaRpcBean);
    }

    private HttpRpcClient getHttpRpcClient(String name) {
        return new HttpRpcClient()
                .setPort(rpcConfiguration.getDefaultHttpport())
                .setHosts(eurekaConfiguration.getHosts(name))
                .setHost(rpcConfiguration.getDefaultIp());
    }

    private NettyRpcClient getNettyRpcClient(NettyRpcBean nettyRpcBean) {
        return new NettyRpcClient().setNettyRpcBean(nettyRpcBean);
    }

    private SocketBioRpcClient getSocketBioRpcClient(String name) {
        return new SocketBioRpcClient()
                .setHost(rpcConfiguration.getDefaultIp())
                .setHosts(eurekaConfiguration.getHosts(name))
                .setPort(rpcConfiguration.getDefaultPort());
    }

    private SocketNioRpcClient getSocketNioRpcClient(NioRpcBean nioRpcBean) {
        return new SocketNioRpcClient().setNioRpcBean(nioRpcBean);
    }
}
