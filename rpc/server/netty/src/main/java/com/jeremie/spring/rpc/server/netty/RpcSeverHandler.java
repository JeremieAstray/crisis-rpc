package com.jeremie.spring.rpc.server.netty;

import com.jeremie.spring.rpc.server.common.RpcHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * @author guanhong 15/10/25 下午3:58.
 */
public class RpcSeverHandler extends SimpleChannelInboundHandler<Object> {

    private Logger logger = Logger.getLogger(this.getClass());

    private ApplicationContext applicationContext;

    public RpcSeverHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcHandler.setRpcContextAddress(ctx.channel().localAddress(),ctx.channel().remoteAddress());
        ctx.writeAndFlush(RpcHandler.handleMessage(msg, applicationContext));
        logger.info("成功写出数据!!");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info(ctx.channel().remoteAddress() + "客户端连接");
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
    }
}
