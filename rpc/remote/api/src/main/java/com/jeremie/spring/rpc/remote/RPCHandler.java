package com.jeremie.spring.rpc.remote;

import com.jeremie.spring.rpc.dto.RpcReceive;

/**
 * @author guanhong 15/11/17 下午5:52.
 */
public class RpcHandler {

    public static void handleMessage(Object message) {
        if (message instanceof RpcReceive) {
            RpcReceive rpcReceive = (RpcReceive) message;
            if (rpcReceive.getStatus() == RpcReceive.Status.SUCCESS) {
                if (rpcReceive.getReturnPara() != null)
                    RpcClient.resultCache.put(rpcReceive.getClientId(), rpcReceive.getReturnPara());
                Object lock = RpcClient.lockMap.get(rpcReceive.getClientId());
                if (lock != null)
                    synchronized (lock) {
                        lock.notify();
                    }
            }
        }
    }
}
