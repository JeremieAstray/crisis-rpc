package com.jeremie.spring.rpc.config;


import com.jeremie.spring.rpc.cluster.EurekaConfiguration;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    public RpcClient getRpcClient() {
        switch (rpcConfiguration.getConnectionMethod()) {
            case "mina":
                return this.getMinaRpcClient();
            case "http":
                return this.getHttpRpcClient();
            case "netty":
                return this.getNettyRpcClient();
            case "bio":
                return this.getSocketBioRpcClient();
            case "nio":
                return this.getSocketNioRpcClient();
            default:
                return this.getMinaRpcClient();
        }
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
