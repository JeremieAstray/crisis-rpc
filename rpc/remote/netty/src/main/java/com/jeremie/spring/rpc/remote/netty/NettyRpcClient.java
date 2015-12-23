package com.jeremie.spring.rpc.remote.netty;


import com.jeremie.spring.rpc.dto.RpcDto;
import com.jeremie.spring.rpc.remote.RpcClient;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class NettyRpcClient extends RpcClient {

    private NettyRpcBean nettyRpcBean;

    public NettyRpcClient setNettyRpcBean(NettyRpcBean nettyRpcBean) {
        this.nettyRpcBean = nettyRpcBean;
        return this;
    }

    @Override
    public Object invoke(RpcDto rpcDto) {
        Object returnObject = this.dynamicProxyObject(rpcDto);
        if (!nettyRpcBean.isConnect())
            nettyRpcBean.init();
        nettyRpcBean.channel.writeAndFlush(rpcDto);
        return returnObject == null ? this.getObject(rpcDto) : returnObject;
    }
}
