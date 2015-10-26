package com.jeremie.spring.rpc.netty;

import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.dto.RPCReceive;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

/**
 * @author guanhong 15/10/25 下午3:58.
 */
public class RPCSeverHandler extends SimpleChannelInboundHandler<Object> {

    private Logger logger = Logger.getLogger(this.getClass());

    private ApplicationContext applicationContext;

    public RPCSeverHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        RPCReceive rpcReceive = new RPCReceive();
        if (msg instanceof RPCDto) {
            RPCDto rpcDto = (RPCDto) msg;
            try {
                Class clazz = Class.forName(rpcDto.getDestClazz());
                Object o1 = applicationContext.getBean(clazz);
                Method method = clazz.getMethod(rpcDto.getMethod(), rpcDto.getParamsType());
                Object result = method.invoke(o1, rpcDto.getParams());
                rpcReceive.setReturnPara(result);
                rpcReceive.setStatus(RPCReceive.Status.SUCCESS);
                rpcReceive.setClientId(rpcDto.getClientId());
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
                rpcReceive.setClientId(rpcDto.getClientId());
                rpcReceive.setStatus(RPCReceive.Status.ERR0R);
                rpcReceive.setReturnPara(null);
            }
        } else {
            rpcReceive.setReturnPara(null);
            rpcReceive.setStatus(RPCReceive.Status.ERR0R);
        }
        ctx.writeAndFlush(rpcReceive);
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
