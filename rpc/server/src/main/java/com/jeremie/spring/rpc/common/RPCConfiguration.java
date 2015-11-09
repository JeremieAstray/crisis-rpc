package com.jeremie.spring.rpc.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanhong on 2015/11/9 16:25
 */
@Configuration
public class RPCConfiguration {

    public static int SERVER_PORT;

    @Value("${rpc.server.port}")
    private void setServerPort(int serverPort){
        SERVER_PORT = serverPort;
    }
}
