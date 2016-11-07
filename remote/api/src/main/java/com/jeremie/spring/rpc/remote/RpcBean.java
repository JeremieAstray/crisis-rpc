package com.jeremie.spring.rpc.remote;

import com.jeremie.spring.rpc.RpcInvocation;
import org.springframework.beans.factory.DisposableBean;

/**
 * @author guanhong on 2015/11/10 17:54
 */
public abstract class RpcBean implements DisposableBean {
    protected String host;
    protected int port;
    protected String appName;
    protected int clientPort;

    public abstract void write(RpcInvocation rpcInvocation);

    public abstract void init();

    public abstract boolean isConnect();

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

}
