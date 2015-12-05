package com.jeremie.spring.rpc.server.mina;

import com.jeremie.spring.rpc.server.common.MonitorStatus;
import com.jeremie.spring.rpc.server.common.RpcHandler;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.context.ApplicationContext;

import java.net.InetSocketAddress;

/**
 * @author guanhong 15/10/25 下午3:58.
 */
public class RpcSeverHandler extends IoHandlerAdapter {

    private  Logger logger = Logger.getLogger(this.getClass());

    private ApplicationContext applicationContext;

    public RpcSeverHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception {
        logger.error(cause.getMessage(),cause);
    }

    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception {
        RpcHandler.setRpcContextAddress(session.getLocalAddress(),session.getRemoteAddress());
        session.write(RpcHandler.handleMessage(message,applicationContext));
    }

    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception {
        logger.info("IDLE " + session.getIdleCount( status ));
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress)session.getRemoteAddress();
        MonitorStatus.remoteHostsList.add(remoteAddress.getHostName() + ":" + remoteAddress.getHostString() + ":" + remoteAddress.getPort());
        super.sessionCreated(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress)session.getRemoteAddress();
        MonitorStatus.remoteHostsList.remove(remoteAddress.getHostName() + ":" + remoteAddress.getHostString() + ":" + remoteAddress.getPort());
        super.sessionClosed(session);
    }
}
