package com.jeremie.spring.rpc.remote.http;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author guanhong 15/10/18 下午11:58.
 */
public class HttpRpcClient extends RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpRpcClient.class);
    private String host;
    private int port;
    private String appName;
    private Executor executor = Executors.newFixedThreadPool(200);

    public HttpRpcClient setHost(String host) {
        this.host = host;
        return this;
    }

    public HttpRpcClient setAppName(String appName) {
        this.appName = appName;
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
