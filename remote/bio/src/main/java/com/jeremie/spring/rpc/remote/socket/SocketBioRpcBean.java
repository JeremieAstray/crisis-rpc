package com.jeremie.spring.rpc.remote.socket;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;

/**
 * Created by jeremie on 2016/11/8.
 */
public class SocketBioRpcBean extends RpcBean {

    public static SocketPool<SocketBioRpcThread> socketBioRpcThreadSocketPool;
    private boolean isConnect = false;


    @Override
    public void write(RpcInvocation rpcInvocation) throws Exception {
        socketBioRpcThreadSocketPool.getConnection().handleObject(rpcInvocation);
    }

    @Override
    public synchronized void init() throws Exception {
        socketBioRpcThreadSocketPool = new SocketPool<>(() -> new SocketBioRpcThread(this.getPort(), this.getHost()));
        isConnect = true;
    }

    @Override
    public boolean isConnect() {
        return isConnect;
    }

    @Override
    public void destroy() throws Exception {
        socketBioRpcThreadSocketPool.killConnection();
    }
}
