package com.jeremie.spring.rpc.remote.mina;


import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.apache.log4j.Logger;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class MinaRpcClient extends RpcClient {

    protected Logger logger = Logger.getLogger(this.getClass());

    private MinaRpcBean minaRpcBean;

    public MinaRpcClient setMinaRpcBean(MinaRpcBean minaRpcBean) {
        this.minaRpcBean = minaRpcBean;
        return this;
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) {
        Object returnObject = this.dynamicProxyObject(rpcInvocation);
        if (!minaRpcBean.isConnect())
            try {
                minaRpcBean.init();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        minaRpcBean.getSession().write(rpcInvocation);
        return returnObject == null ? this.getObject(rpcInvocation) : returnObject;
    }
}
