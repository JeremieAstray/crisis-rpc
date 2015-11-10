package com.jeremie.spring.rpc.commons;


import com.jeremie.spring.rpc.http.HttpRPCClient;
import com.jeremie.spring.rpc.mina.MinaRPCBean;
import com.jeremie.spring.rpc.mina.MinaRPCClient;
import com.jeremie.spring.rpc.netty.NettyRPCBean;
import com.jeremie.spring.rpc.netty.NettyRPCClient;
import com.jeremie.spring.rpc.nio.NioRPCBean;
import com.jeremie.spring.rpc.nio.SocketNioRPCClient;
import com.jeremie.spring.rpc.socket.SocketBioRPCClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanhong 15/10/24 上午11:42.
 */
@Configuration
public class RPCFactory {
    @Autowired
    private RPCConfiguration rpcConfiguration;
    @Autowired
    private RPCBean rpcBean;

    @Bean
    public RPCClient getRPCClient() {
        switch (rpcConfiguration.getConnectMethod()) {
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
                .setPort(rpcConfiguration.getDefaultHttpPort())
                .setHosts(rpcConfiguration.getHosts())
                .setHost(rpcConfiguration.getDefaultIP());
    }

    private NettyRPCClient getNettyRPCClient() {
        return new NettyRPCClient().setNettyRPCBean((NettyRPCBean) rpcBean);
    }

    private SocketBioRPCClient getSocketBioRPCClient() {
        return new SocketBioRPCClient()
                .setHost(rpcConfiguration.getDefaultIP())
                .setHosts(rpcConfiguration.getHosts())
                .setPort(rpcConfiguration.getDefaultPort());
    }

    private SocketNioRPCClient getSocketNioRPCClient() {
        return new SocketNioRPCClient().setNioRPCBean((NioRPCBean) rpcBean);
    }
}
