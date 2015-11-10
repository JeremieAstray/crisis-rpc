package com.jeremie.spring.rpc.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author guanhong on 2015/11/9 16:25
 */
@Component
public class RPCConfiguration {

    @Value("${rpc.server.port}")
    private int serverPort;

    public int getServerPort() {
        return serverPort;
    }
}
