package com.jeremie.spring.rpc.remote;

import com.jeremie.spring.rpc.remote.cluster.EurekaLoadBalance;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.client.ServiceInstance;

/**
 * @author guanhong on 2015/11/10 17:54
 */
public abstract class RpcBean implements DisposableBean {
    protected String host;
    protected int port;
    protected EurekaLoadBalance eurekaLoadBalance;
    protected String appName;
    protected int clientPort;

    public void init() {
        ServiceInstance serviceInstance = eurekaLoadBalance.doSelect(appName);
        if (serviceInstance != null) {
            this.host = serviceInstance.getHost();
            this.port = serviceInstance.getPort();
        }
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setEurekaLoadBalance(EurekaLoadBalance eurekaLoadBalance) {
        this.eurekaLoadBalance = eurekaLoadBalance;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

}
