package com.jeremie.spring.rpc.remote.netty;

import com.jeremie.spring.rpc.remote.RpcHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

/**
 * @author guanhong 15/10/25 下午4:10.
 */
public class NettyRpcHandler extends SimpleChannelInboundHandler<Object> {
    private Logger logger = Logger.getLogger(this.getClass());


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcHandler.handleMessage(msg);
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
