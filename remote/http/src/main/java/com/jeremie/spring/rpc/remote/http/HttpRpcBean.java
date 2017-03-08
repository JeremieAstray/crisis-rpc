package com.jeremie.spring.rpc.remote.http;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jeremie on 2016/11/8.
 */
public class HttpRpcBean extends RpcBean {

    private ExecutorService executor = Executors.newFixedThreadPool(200);

    @Override
    public void write(RpcInvocation rpcInvocation) throws Exception {
        this.executor.execute(new HttpRpcThread(this.getPort(), this.getHost(), rpcInvocation));
    }

    @Override
    public void init() throws Exception {
        this.executor.execute(new HttpRpcThread(this.getPort(), this.getHost(), null));
    }

    @Override
    public boolean isConnect() {
        return false;
    }

    @Override
    public void destroy() throws Exception {
        this.executor.shutdown();
    }
}
