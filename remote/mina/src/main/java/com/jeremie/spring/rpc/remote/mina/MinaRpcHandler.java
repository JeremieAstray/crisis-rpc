package com.jeremie.spring.rpc.remote.mina;

import com.jeremie.spring.rpc.remote.RpcHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author guanhong 15/10/25 下午4:10.
 */
public class MinaRpcHandler extends IoHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MinaRpcHandler.class);

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        logger.info("IDLE " + session.getIdleCount(status));
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        RpcHandler.handleMessage(message);
    }

}
