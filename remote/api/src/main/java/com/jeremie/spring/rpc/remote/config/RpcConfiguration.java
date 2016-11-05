package com.jeremie.spring.rpc.remote.config;


import java.util.ArrayList;
import java.util.List;

/**
 * @author guanhong 15/10/24 下午9:32.
 */
public class RpcConfiguration {

    private String defaultIp;
    private int defaultPort;
    private int defaultHttpport;
    private int defaultNioClientPort;
    private List<ServiceConfig> services = new ArrayList<>();

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

    public List<ServiceConfig> getServices() {
        return services;
    }

    public void setServices(List<ServiceConfig> services) {
        this.services = services;
    }
}
