package com.jeremie.spring.rpc.remote.http;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;

/**
 * Created by jeremie on 2016/11/8.
 */
public class HttpRpcBean extends RpcBean {
    @Override
    public void write(RpcInvocation rpcInvocation) throws Exception {

    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public boolean isConnect() {
        return false;
    }

    @Override
    public void destroy() throws Exception {

    }
}
