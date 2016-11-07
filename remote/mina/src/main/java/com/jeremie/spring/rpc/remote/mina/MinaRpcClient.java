package com.jeremie.spring.rpc.remote.mina;


import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class MinaRpcClient extends RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(MinaRpcClient.class);

    private RpcBean minaRpcBean;

    @Override
    public void setRpcBean(RpcBean rpcBean) {
        this.minaRpcBean = rpcBean;
    }

    @Override
    public RpcBean getRpcBean() {
        return this.minaRpcBean;
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
        return returnObject == null ? this.getObject(rpcInvocation) : returnObject;
    }
}
