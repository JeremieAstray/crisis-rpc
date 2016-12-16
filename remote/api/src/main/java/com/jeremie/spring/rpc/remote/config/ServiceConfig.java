package com.jeremie.spring.rpc.remote.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guanhong on 2015/12/2 15:32
 */
public class ServiceConfig implements Serializable {

    private String defaultIp;
    private int defaultPort;
    private int defaultNioClientPort;
    private boolean lazyLoading = false;
    private Long loadTimeout = 500L;

    private String name;

    private String method;
    private List<String> packages = new ArrayList<>();
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }

    public String getDefaultIp() {
        return this.defaultIp;
    }

    public void setDefaultIp(String defaultIp) {
        this.defaultIp = defaultIp;
    }

    public int getDefaultPort() {
        return this.defaultPort;
    }

    public void setDefaultPort(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    public int getDefaultNioClientPort() {
        return this.defaultNioClientPort;
    }

    public void setDefaultNioClientPort(int defaultNioClientPort) {
        this.defaultNioClientPort = defaultNioClientPort;
    }

    public boolean isLazyLoading() {
        return this.lazyLoading;
    }

    public void setLazyLoading(boolean lazyLoading) {
        this.lazyLoading = lazyLoading;
    }

    public Long getLoadTimeout() {
        return this.loadTimeout;
    }

    public void setLoadTimeout(Long loadTimeout) {
        this.loadTimeout = loadTimeout;
    }
}