package com.jeremie.spring.rpc.netty;

import com.jeremie.spring.rpc.dto.RPCReceive;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

/**
 * @author guanhong 15/10/25 下午4:10.
 */
public class RPCClientHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = Logger.getLogger(this.getClass());



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        if(msg instanceof RPCReceive){
            RPCReceive rpcReceive = (RPCReceive) msg;
            if (rpcReceive.getStatus() == RPCReceive.Status.SUCCESS){
                if(rpcReceive.getReturnPara() != null)
                   NettyRPCClient.resultMap.put(rpcReceive.getClientId(),rpcReceive.getReturnPara());
                Thread thread = NettyRPCClient.threadMap.get(rpcReceive.getClientId());
                synchronized (thread) {
                    thread.notify();
                }
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("error", cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
