package com.jeremie.spring.rpc.remote.socket;


import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class SocketBioRpcClient extends RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketBioRpcClient.class);
    private RpcBean rpcBean;
    private Executor executor = Executors.newFixedThreadPool(200);

    @Override
    public void setRpcBean(RpcBean rpcBean) {
        this.rpcBean = rpcBean;
    }

    @Override
    public RpcBean getRpcBean() {
        return this.rpcBean;
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) {
        executor.execute(new SocketBioRpcThread(this.rpcBean.getPort(), this.rpcBean.getHost(), rpcInvocation));
        Object returnObject = this.dynamicProxyObject(rpcInvocation);
        return returnObject == null ? this.getObject(rpcInvocation) : returnObject;
    }
}
