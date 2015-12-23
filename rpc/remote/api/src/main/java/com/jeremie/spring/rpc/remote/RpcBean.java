package com.jeremie.spring.rpc.remote;

import org.springframework.beans.factory.DisposableBean;

import java.util.List;

/**
 * @author guanhong on 2015/11/10 17:54
 */
public abstract class RpcBean implements DisposableBean {
    protected String host;
    protected int port;
    protected List<String> hosts;
    protected int clientPort;

    public abstract void init();

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }
}
