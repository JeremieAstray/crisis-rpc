package com.jeremie.spring.rpc.remote.http;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.cluster.EurekaHelper;
import com.jeremie.spring.rpc.loadBalance.LoadBalance;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.apache.log4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author guanhong 15/10/18 下午11:58.
 */
public class HttpRpcClient extends RpcClient {
    private Logger logger = Logger.getLogger(this.getClass());

    private String host;
    private int port;
    private EurekaHelper eurekaHelper;
    private String appName;
    private LoadBalance loadBalance;
    private Executor executor = Executors.newFixedThreadPool(200);

    public HttpRpcClient setEurekaHelper(EurekaHelper eurekaHelper) {
        this.eurekaHelper = eurekaHelper;
        return this;
    }

    public HttpRpcClient setHost(String host) {
        this.host = host;
        return this;
    }

    public HttpRpcClient setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public HttpRpcClient setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
        return this;
    }

    public HttpRpcClient setPort(int port) {
        this.port = port;
        return this;
    }


    @Override
    public Object invoke(RpcInvocation rpcInvocation) {
        Object returnObject = this.dynamicProxyObject(rpcInvocation);
        executor.execute(new HttpRpcThread(port, host, rpcInvocation));
        return returnObject == null ? this.getObject(rpcInvocation) : returnObject;
    }
}
