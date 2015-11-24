package com.jeremie.spring.rpc.remote.mina;

import com.jeremie.spring.rpc.RpcContext;
import com.jeremie.spring.rpc.RpcHandler;
import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.dto.RPCReceive;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

/**
 * @author guanhong 15/10/25 下午3:58.
 */
public class RPCSeverHandler extends IoHandlerAdapter {

    private  Logger logger = Logger.getLogger(this.getClass());

    private ApplicationContext applicationContext;

    public RPCSeverHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception {
        logger.error(cause.getMessage(),cause);
    }

    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception {
        RpcHandler.setRPCContextAddress(session.getLocalAddress(),session.getRemoteAddress());
        session.write(RpcHandler.handleMessage(message,applicationContext));
    }

    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception {
        logger.info("IDLE " + session.getIdleCount( status ));
    }
}
