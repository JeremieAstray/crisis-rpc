package com.jeremie.spring.rpc.commons;

import com.jeremie.spring.rpc.dto.RPCDto;

/**
 * @author guanhong 15/10/24 上午11:43.
 */
public interface RPCClient {
    public Object invoke(RPCDto rpcDto);
}
