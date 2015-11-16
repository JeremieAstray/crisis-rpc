package com.jeremie.spring.rpc.protocol.mina;

import com.jeremie.spring.rpc.config.RPCClient;
import com.jeremie.spring.rpc.dto.RPCReceive;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.util.concurrent.locks.Lock;

/**
 * @author guanhong 15/10/25 下午4:10.
 */
public class RPCClientHandler extends IoHandlerAdapter {
    private Logger logger = Logger.getLogger(this.getClass());

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
        if (message instanceof RPCReceive) {
            RPCReceive rpcReceive = (RPCReceive) message;
            if (rpcReceive.getStatus() == RPCReceive.Status.SUCCESS) {
                if (rpcReceive.getReturnPara() != null)
                    MinaRPCClient.resultMap.put(rpcReceive.getClientId(), rpcReceive.getReturnPara());
                Lock lock = RPCClient.lockMap.get(rpcReceive.getClientId());
                if (lock != null) {
                    lock.unlock();
                }
            }
        }

    }

}
