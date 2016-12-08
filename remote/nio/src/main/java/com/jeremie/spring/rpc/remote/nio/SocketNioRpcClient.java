package com.jeremie.spring.rpc.remote.nio;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author guanhong 15/10/24 下午12:47.
 */
public class SocketNioRpcClient extends RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketNioRpcClient.class);

    private RpcBean nioRpcBean;

    public SocketNioRpcClient(Boolean lazyLoading) {
        super(lazyLoading);
    }

    @Override
    public RpcBean getRpcBean() {
        return this.nioRpcBean;
    }

    @Override
    public void setRpcBean(RpcBean rpcBean) {
        this.nioRpcBean = rpcBean;
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) throws Exception {
        nioRpcBean.write(rpcInvocation);
        if (super.lazyLoading) {
            Object returnObject = this.dynamicProxyObject(rpcInvocation);
            return returnObject == null ? this.getObject(rpcInvocation) : returnObject;
        } else {
            return this.getObject(rpcInvocation);
        }
    }

}
