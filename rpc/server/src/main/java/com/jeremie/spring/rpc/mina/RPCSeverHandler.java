package com.jeremie.spring.rpc.mina;

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
        logger.error("error",cause);
    }

    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception {
        RPCReceive rpcReceive = new RPCReceive();
        if (message instanceof RPCDto) {
            RPCDto rpcDto = (RPCDto) message;
            Class clazz = Class.forName(rpcDto.getDestClazz());
            Object o1 = applicationContext.getBean(clazz);
            Method method = clazz.getMethod(((RPCDto) message).getMethod(), ((RPCDto) message).getParamsType());
            Object result = method.invoke(o1, ((RPCDto) message).getParams());
            rpcReceive.setReturnPara(result);
            rpcReceive.setStatus(RPCReceive.Status.SUCCESS);
            rpcReceive.setClientId(rpcDto.getClientId());
        } else {
            rpcReceive.setReturnPara(null);
            rpcReceive.setStatus(RPCReceive.Status.ERR0R);
        }
        session.write(rpcReceive);
    }

    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception {
        logger.info("IDLE " + session.getIdleCount( status ));
    }
}
