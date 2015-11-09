package com.jeremie.spring.rpc.commons;

import com.jeremie.spring.rpc.RPCClient;
import com.jeremie.spring.rpc.RPCFactory;
import com.jeremie.spring.rpc.mina.MinaRPCBean;
import com.jeremie.spring.rpc.netty.NettyRPCBean;
import com.jeremie.spring.rpc.nio.RPCNioBean;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guanhong 15/10/24 下午9:32.
 */
@Configuration
public class RPCConfiguration {

    public static String DEFAULT_IP;
    public static int DEFAULT_PORT;
    public static int DEFAULT_HTTPPORT;
    public static int DEFAULT_NIO_CLIENT_PORT;
    private static String CONNECT_METHOD;

    @Value("${rpc.default.ip}")
    private void setDefaultIp(String defaultIp) {
        DEFAULT_IP = defaultIp;
    }

    @Value("${rpc.default.port}")
    private void setDefaultPort(int defaultPort) {
        DEFAULT_PORT = defaultPort;
    }

    @Value("${rpc.default.httpport}")
    private void setDefaultHttpport(int defaultHttpport) {
        DEFAULT_HTTPPORT = defaultHttpport;
    }

    @Value("${rpc.default.nio.client.port}")
    private void setDefaultNioClientPort(int defaultNioClientPort) {
        DEFAULT_NIO_CLIENT_PORT = defaultNioClientPort;
    }

    @Value("${rpc.connection.method}")
    private void setConnectMethod(String connectMethod) {
        CONNECT_METHOD = connectMethod;
    }

    public static RPCClient getRPCClient(){
        switch (CONNECT_METHOD){
            case "mina":
                return RPCFactory.getMinaRPCClient();
            case "http":
                return RPCFactory.getHttpRPCClient();
            case "netty":
                return RPCFactory.getNettyRPCClient();
            case "bio":
                return RPCFactory.getSocketBioRPCClient();
            case "nio":
                return RPCFactory.getSocketNioRPCClient();
            default:
                return RPCFactory.getMinaRPCClient();
        }
    }

    private static EurekaClient discoverEurekaClient;

    @Autowired
    private void setDiscoverEurekaClient(EurekaClient eurekaClient){
        discoverEurekaClient = eurekaClient;
    }

    public static List<String> getHosts(){
        List<InstanceInfo> instances = discoverEurekaClient.getInstancesByVipAddress("rpc-server", false);
        List<String> hosts = new ArrayList<>();
        instances.forEach(instanceInfo -> hosts.add(instanceInfo.getIPAddr()));
        return hosts;
    }


    @Bean(destroyMethod = "destroy")
    public RPCNioBean rpcNioBean(){
        return new RPCNioBean(getHosts());
    }

    @Bean(destroyMethod = "destroy")
    public MinaRPCBean minaRPCBean(){
        return new MinaRPCBean(getHosts());
    }

    @Bean(destroyMethod = "destroy")
    public NettyRPCBean nettyRPCBean(){
        return new NettyRPCBean(getHosts());
    }
}
