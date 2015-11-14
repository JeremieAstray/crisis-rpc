package com.jeremie.spring.rpc.config;


import com.jeremie.spring.rpc.cluster.EurekaConfiguration;
import com.jeremie.spring.rpc.protocol.http.HttpRPCClient;
import com.jeremie.spring.rpc.protocol.mina.MinaRPCBean;
import com.jeremie.spring.rpc.protocol.mina.MinaRPCClient;
import com.jeremie.spring.rpc.protocol.netty.NettyRPCBean;
import com.jeremie.spring.rpc.protocol.netty.NettyRPCClient;
import com.jeremie.spring.rpc.protocol.nio.SocketNioRPCClient;
import com.jeremie.spring.rpc.protocol.nio.NioRPCBean;
import com.jeremie.spring.rpc.protocol.socket.SocketBioRPCClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanhong 15/10/24 上午11:42.
 */
@Configuration
//@EnableConfigurationProperties(RPCConfiguration.class)
public class RPCFactory {
    @Autowired
    private RPCConfiguration rpcConfiguration;
    @Autowired
    private EurekaConfiguration eurekaConfiguration;
    @Autowired
    private RPCBean rpcBean;

    @Bean
    public RPCClient getRPCClient() {
        switch (rpcConfiguration.getConnectionMethod()) {
            case "mina":
                return this.getMinaRPCClient();
            case "http":
                return this.getHttpRPCClient();
            case "netty":
                return this.getNettyRPCClient();
            case "bio":
                return this.getSocketBioRPCClient();
            case "nio":
                return this.getSocketNioRPCClient();
            default:
                return this.getMinaRPCClient();
        }
    }

    private MinaRPCClient getMinaRPCClient() {
        return new MinaRPCClient().setMinaRPCBean((MinaRPCBean) rpcBean);
    }

    private HttpRPCClient getHttpRPCClient() {
        return new HttpRPCClient()
                .setPort(rpcConfiguration.getDefaultHttpport())
                .setHosts(eurekaConfiguration.getHosts())
                .setHost(rpcConfiguration.getDefaultIp());
    }

    private NettyRPCClient getNettyRPCClient() {
        return new NettyRPCClient().setNettyRPCBean((NettyRPCBean) rpcBean);
    }

    private SocketBioRPCClient getSocketBioRPCClient() {
        return new SocketBioRPCClient()
                .setHost(rpcConfiguration.getDefaultIp())
                .setHosts(eurekaConfiguration.getHosts())
                .setPort(rpcConfiguration.getDefaultPort());
    }

    private SocketNioRPCClient getSocketNioRPCClient() {
        return new SocketNioRPCClient().setNioRPCBean((NioRPCBean) rpcBean);
    }
}
