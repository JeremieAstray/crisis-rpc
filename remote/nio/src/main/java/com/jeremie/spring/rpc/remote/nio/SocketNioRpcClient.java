package com.jeremie.spring.rpc.remote.nio;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author guanhong 15/10/24 下午12:47.
 */
@Component
public class SocketNioRpcClient extends RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketNioRpcClient.class);

    private RpcBean nioRpcBean;

    @Override
    public void setRpcBean(RpcBean rpcBean) {
        this.nioRpcBean = rpcBean;
    }

    @Override
    public RpcBean getRpcBean() {
        return this.nioRpcBean;
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) {
        Object returnObject = this.dynamicProxyObject(rpcInvocation);
        nioRpcBean.write(rpcInvocation);
        return returnObject == null ? this.getObject(rpcInvocation) : returnObject;
    }

}
