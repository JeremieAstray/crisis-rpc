package com.jeremie.spring.rpc.remote.netty;


import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Jeremie on 2015/5/13.
 */
@Component
public class NettyRpcClient extends RpcClient {

    @Autowired
    private NettyRpcBean nettyRpcBean;

    public NettyRpcClient setNettyRpcBean(NettyRpcBean nettyRpcBean) {
        this.nettyRpcBean = nettyRpcBean;
        return this;
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) {
        Object returnObject = this.dynamicProxyObject(rpcInvocation);
        if (!nettyRpcBean.isConnect())
            nettyRpcBean.init();
        nettyRpcBean.channel.writeAndFlush(rpcInvocation);
        return returnObject == null ? this.getObject(rpcInvocation) : returnObject;
    }
}
