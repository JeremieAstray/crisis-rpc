package com.jeremie.spring.rpc.remote;

import com.jeremie.spring.rpc.cluster.EurekaHelper;
import com.jeremie.spring.rpc.loadBalance.LoadBalance;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.beans.factory.DisposableBean;

/**
 * @author guanhong on 2015/11/10 17:54
 */
public abstract class RpcBean implements DisposableBean {
    protected String host;
    protected int port;
    protected EurekaHelper eurekaHelper;
    protected String appName;
    protected LoadBalance loadBalance;
    protected int clientPort;

    public void init() {
        InstanceInfo instanceInfo = loadBalance.select(eurekaHelper.getHosts(appName));
        if (instanceInfo != null)
            this.host = instanceInfo.getIPAddr();
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setEurekaHelper(EurekaHelper eurekaHelper) {
        this.eurekaHelper = eurekaHelper;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }
}
