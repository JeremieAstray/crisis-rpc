package com.jeremie.spring.rpc.protocol.mina;


import com.jeremie.spring.rpc.config.RPCClient;
import com.jeremie.spring.rpc.dto.RPCDto;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class MinaRPCClient implements RPCClient {

    protected Logger logger = Logger.getLogger(this.getClass());

    protected static Map<String, Object> resultMap = new ConcurrentHashMap<>();
    protected static Map<String, Thread> threadMap = new ConcurrentHashMap<>();

    private MinaRPCBean minaRPCBean;

    public MinaRPCClient setMinaRPCBean(MinaRPCBean minaRPCBean) {
        this.minaRPCBean = minaRPCBean;
        return this;
    }

    @Override
    public Object invoke(RPCDto rpcDto) {
        if(!minaRPCBean.isConnect())
            try {
                minaRPCBean.init();
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
                return null;
            }
        Thread current = Thread.currentThread();
        rpcDto.setClientId(UUID.randomUUID().toString());
        threadMap.put(rpcDto.getClientId(),current);
        minaRPCBean.getSession().write(rpcDto);
        try {
            synchronized (current) {
                current.wait(500);
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        Object o = resultMap.get(rpcDto.getClientId());
        resultMap.remove(rpcDto.getClientId());
        threadMap.remove(rpcDto.getClientId());
        return o;
    }
}
