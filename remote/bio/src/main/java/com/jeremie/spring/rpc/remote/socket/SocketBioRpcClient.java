package com.jeremie.spring.rpc.remote.socket;


import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class SocketBioRpcClient extends RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketBioRpcClient.class);
    private RpcBean rpcBean;

    public SocketBioRpcClient(String serverName, Boolean lazyLoading, Long cacheTimeout) {
        super(serverName, lazyLoading, cacheTimeout);
    }

    @Override
    public RpcBean getRpcBean() {
        return this.rpcBean;
    }

    @Override
    public void setRpcBean(RpcBean rpcBean) {
        this.rpcBean = rpcBean;
    }

    @Override
    public void init() throws Exception {
        if (!this.rpcBean.isConnect()) {
            this.rpcBean.init();
        }
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) throws Exception {
        this.init();
        this.rpcBean.write(rpcInvocation);
        if (super.lazyLoading) {
            Object returnObject = this.dynamicProxyObject(rpcInvocation);
            return returnObject == null ? this.getObject(rpcInvocation) : returnObject;
        } else {
            return this.getObject(rpcInvocation);
        }
    }
}
