package com.jeremie.spring.rpc.remote;

import com.jeremie.spring.rpc.dto.RPCReceive;

/**
 * @author guanhong 15/11/17 下午5:52.
 */
public class RPCHandler {

    public static void handleMessage(Object message) {
        if (message instanceof RPCReceive) {
            RPCReceive rpcReceive = (RPCReceive) message;
            if (rpcReceive.getStatus() == RPCReceive.Status.SUCCESS) {
                if (rpcReceive.getReturnPara() != null)
                    RPCClient.resultMap.put(rpcReceive.getClientId(), rpcReceive.getReturnPara());
                Object lock = RPCClient.lockMap.get(rpcReceive.getClientId());
                if (lock != null)
                    synchronized (lock) {
                        lock.notify();
                    }
            }
        }
    }
}
