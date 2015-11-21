package com.jeremie.spring.rpc.remote.mina;


import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.remote.RPCClient;
import org.apache.log4j.Logger;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class MinaRPCClient extends RPCClient {

    protected Logger logger = Logger.getLogger(this.getClass());

    private MinaRPCBean minaRPCBean;

    public MinaRPCClient setMinaRPCBean(MinaRPCBean minaRPCBean) {
        this.minaRPCBean = minaRPCBean;
        return this;
    }

    @Override
    public Object invoke(RPCDto rpcDto) {
        if (!minaRPCBean.isConnect())
            try {
                minaRPCBean.init();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        minaRPCBean.getSession().write(rpcDto);
        return this.dynamicProxyObject(rpcDto);
    }
}
