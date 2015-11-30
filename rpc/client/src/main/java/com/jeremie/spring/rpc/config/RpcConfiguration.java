package com.jeremie.spring.rpc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author guanhong 15/10/24 下午9:32.
 */
@ConfigurationProperties("com.jeremie.rpc")
public class RpcConfiguration {

    private String defaultIp;
    private int defaultPort;
    private int defaultHttpport;
    private int defaultNioClientPort;
    private String connectionMethod;

    public String getDefaultIp() {
        return defaultIp;
    }

    public void setDefaultIp(String defaultIp) {
        this.defaultIp = defaultIp;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public void setDefaultPort(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    public int getDefaultHttpport() {
        return defaultHttpport;
    }

    public void setDefaultHttpport(int defaultHttpport) {
        this.defaultHttpport = defaultHttpport;
    }

    public int getDefaultNioClientPort() {
        return defaultNioClientPort;
    }

    public void setDefaultNioClientPort(int defaultNioClientPort) {
        this.defaultNioClientPort = defaultNioClientPort;
    }

    public String getConnectionMethod() {
        return connectionMethod;
    }

    public void setConnectionMethod(String connectionMethod) {
        this.connectionMethod = connectionMethod;
    }

}
