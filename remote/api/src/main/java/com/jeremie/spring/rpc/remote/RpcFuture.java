package com.jeremie.spring.rpc.remote;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.proxy.ProxyHandler;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author guanhong 2019/4/19.
 */
public class RpcFuture implements Future {

    private RpcInvocation rpcInvocation;
    private ProxyHandler proxyHandler;

    public RpcFuture(RpcInvocation rpcInvocation, ProxyHandler proxyHandler) {
        this.rpcInvocation = rpcInvocation;
        this.proxyHandler = proxyHandler;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return proxyHandler.getObject() != null;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return get(RpcClient.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        try {
            if (proxyHandler.isFirst() && proxyHandler.getObject() == null) {
                try {
                    Object o = RpcClient.getCache(rpcInvocation.getServerName()).getIfPresent(rpcInvocation.getClientId());
                    if (o != null) {
                        proxyHandler.setObject(o);
                    } else {
                        synchronized (this) {
                            this.wait(unit.toMillis(timeout));
                        }
                        o = RpcClient.getCache(rpcInvocation.getServerName()).getIfPresent(rpcInvocation.getClientId());
                    }
                    if (o != null) {
                        proxyHandler.setObject(o);
                    }
                    proxyHandler.setFirst(false);
                } finally {
                    RpcClient.lockMap.remove(rpcInvocation.getClientId());
                }
            }
            return proxyHandler.getObject();
        } catch (Exception e) {
            throw e;
        } finally {
            RpcClient.getCache(rpcInvocation.getServerName()).invalidate(rpcInvocation.getClientId());
        }
    }
}
