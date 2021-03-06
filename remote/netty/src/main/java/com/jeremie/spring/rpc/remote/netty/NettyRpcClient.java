package com.jeremie.spring.rpc.remote.netty;


import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;
import com.jeremie.spring.rpc.remote.RpcClient;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class NettyRpcClient extends RpcClient {

    private RpcBean nettyRpcBean;

    public NettyRpcClient(String serverName, Boolean lazyLoading, Long cacheTimeout) {
        super(serverName, lazyLoading, cacheTimeout);
    }

    @Override
    public RpcBean getRpcBean() {
        return this.nettyRpcBean;
    }

    @Override
    public void setRpcBean(RpcBean rpcBean) {
        this.nettyRpcBean = rpcBean;

    }

    @Override
    public void init() throws Exception {
        if (!this.nettyRpcBean.isConnect()) {
            this.nettyRpcBean.init();
        }
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) throws Exception {
        this.init();
        this.nettyRpcBean.write(rpcInvocation);
        if (super.lazyLoading) {
            Object returnObject = this.dynamicProxyObject(rpcInvocation);
            return returnObject == null ? this.getObject(rpcInvocation) : returnObject;
        } else {
            return this.getObject(rpcInvocation);
        }
    }
}
