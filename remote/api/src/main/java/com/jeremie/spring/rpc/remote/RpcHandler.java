package com.jeremie.spring.rpc.remote;

import com.jeremie.spring.rpc.RpcResult;

/**
 * @author guanhong 15/11/17 下午5:52.
 */
public class RpcHandler {

    public static void handleMessage(Object message) {
        if (message instanceof RpcResult) {
            RpcResult rpcResult = (RpcResult) message;
            if (rpcResult.getStatus() == RpcResult.Status.SUCCESS) {
                if (rpcResult.getReturnPara() != null) {
                    RpcClient.resultCache.put(rpcResult.getClientId(), rpcResult.getReturnPara());
                }
                Object lock = RpcClient.lockMap.get(rpcResult.getClientId());
                if (lock != null) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            }
        }
    }
}
