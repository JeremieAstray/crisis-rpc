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
import com.jeremie.spring.rpc.remote.nio.SocketNioRpcClient;
import com.jeremie.spring.rpc.remote.nio.NioRpcBean;
import com.jeremie.spring.rpc.remote.socket.SocketBioRpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

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
    private RpcBean rpcBean;
    @Autowired
    private RpcInitializer rpcInitializer;

    @Bean
    public RpcClient getRpcClient() {
        RpcClient rpcClient;
        switch (rpcConfiguration.getServices().get(0).getMethod()) {
            case "mina":
                rpcClient = this.getMinaRpcClient();
                break;
            case "http":
                rpcClient = this.getHttpRpcClient();
                break;
            case "netty":
                rpcClient = this.getNettyRpcClient();
                break;
            case "bio":
                rpcClient = this.getSocketBioRpcClient();
                break;
            case "nio":
                rpcClient =  this.getSocketNioRpcClient();
                break;
            default:
                rpcClient =  this.getMinaRpcClient();
                break;
        }
        rpcInitializer.setRpcClient(rpcClient);
        return rpcClient;
    }

    private MinaRpcClient getMinaRpcClient() {
        return new MinaRpcClient().setMinaRpcBean((MinaRpcBean) rpcBean);
    }

    private HttpRpcClient getHttpRpcClient() {
        return new HttpRpcClient()
                .setPort(rpcConfiguration.getDefaultHttpport())
                .setHosts(eurekaConfiguration.getHosts())
                .setHost(rpcConfiguration.getDefaultIp());
    }

    private NettyRpcClient getNettyRpcClient() {
        return new NettyRpcClient().setNettyRpcBean((NettyRpcBean) rpcBean);
    }

    private SocketBioRpcClient getSocketBioRpcClient() {
        return new SocketBioRpcClient()
                .setHost(rpcConfiguration.getDefaultIp())
                .setHosts(eurekaConfiguration.getHosts())
                .setPort(rpcConfiguration.getDefaultPort());
    }

    private SocketNioRpcClient getSocketNioRpcClient() {
        return new SocketNioRpcClient().setNioRpcBean((NioRpcBean) rpcBean);
    }
}
