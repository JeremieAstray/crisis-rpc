package com.jeremie.spring.rpc.remote.netty;


import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.remote.RPCClient;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class NettyRPCClient extends RPCClient {

    private NettyRPCBean nettyRPCBean;

    public NettyRPCClient setNettyRPCBean(NettyRPCBean nettyRPCBean) {
        this.nettyRPCBean = nettyRPCBean;
        return this;
    }

    @Override
    public Object invoke(RPCDto rpcDto) {
        if (!nettyRPCBean.isConnect())
            nettyRPCBean.init();
        nettyRPCBean.channel.writeAndFlush(rpcDto);
        return this.dynamicProxyObject(rpcDto);
    }
}
