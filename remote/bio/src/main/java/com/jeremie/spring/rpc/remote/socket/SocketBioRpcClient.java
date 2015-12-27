package com.jeremie.spring.rpc.remote.socket;


import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcClient;
import com.jeremie.spring.rpc.remote.cluster.EurekaLoadBalance;
import org.apache.log4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class SocketBioRpcClient extends RpcClient {

    protected Logger logger = Logger.getLogger(this.getClass());
    private String host;
    private int port;
    private EurekaLoadBalance eurekaLoadBalance;
    private String AppName;
    private Executor executor = Executors.newFixedThreadPool(200);

    public SocketBioRpcClient setHost(String host) {
        this.host = host;
        return this;
    }

    public SocketBioRpcClient setPort(int port) {
        this.port = port;
        return this;
    }

    public SocketBioRpcClient setAppName(String appName) {
        AppName = appName;
        return this;
    }

    public SocketBioRpcClient setEurekaLoadBalance(EurekaLoadBalance eurekaLoadBalance) {
        this.eurekaLoadBalance = eurekaLoadBalance;
        return this;
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) {
        Object returnObject = this.dynamicProxyObject(rpcInvocation);
        executor.execute(new SocketBioRpcThread(port, host, rpcInvocation));
        return returnObject == null ? this.getObject(rpcInvocation) : returnObject;
    }
}
