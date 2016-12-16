package com.jeremie.spring.rpc.remote.http;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;
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
    private RpcBean rpcBean;
    private Executor executor = Executors.newFixedThreadPool(200);

    public HttpRpcClient(String serverName, Boolean lazyLoading, Long cacheTimeout) {
        super(serverName, lazyLoading, cacheTimeout);
    }

    @Override
    public RpcBean getRpcBean() {
        return this.rpcBean;
    }

    @Override
    public void setRpcBean(RpcBean rpcBean) {
        this.rpcBean = rpcBean;
    }

    @Override
    public void init() throws Exception {
        executor.execute(new HttpRpcThread(this.rpcBean.getPort(), this.rpcBean.getHost(), null));
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) {
        executor.execute(new HttpRpcThread(this.rpcBean.getPort(), this.rpcBean.getHost(), rpcInvocation));
        if (super.lazyLoading) {
            Object returnObject = this.dynamicProxyObject(rpcInvocation);
            return returnObject == null ? this.getObject(rpcInvocation) : returnObject;
        } else {
            return this.getObject(rpcInvocation);
        }
    }
}
