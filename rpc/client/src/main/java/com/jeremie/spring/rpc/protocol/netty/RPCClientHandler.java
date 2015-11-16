package com.jeremie.spring.rpc.protocol.netty;

import com.jeremie.spring.rpc.config.RPCClient;
import com.jeremie.spring.rpc.dto.RPCReceive;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

/**
 * @author guanhong 15/10/25 下午4:10.
 */
public class RPCClientHandler extends SimpleChannelInboundHandler<Object> {
    private Logger logger = Logger.getLogger(this.getClass());


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RPCReceive) {
            RPCReceive rpcReceive = (RPCReceive) msg;
            if (rpcReceive.getStatus() == RPCReceive.Status.SUCCESS) {
                if (rpcReceive.getReturnPara() != null)
                    NettyRPCClient.resultMap.put(rpcReceive.getClientId(), rpcReceive.getReturnPara());
                Object lock = RPCClient.lockMap.get(rpcReceive.getClientId());
                if (lock != null)
                    synchronized (lock) {
                        lock.notify();
                    }
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客服端连接服务器" + ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
    }
}
