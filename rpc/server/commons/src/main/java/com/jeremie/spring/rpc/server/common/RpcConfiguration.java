package com.jeremie.spring.rpc.server.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author guanhong on 2015/11/9 16:25
 */
@ConfigurationProperties("com.jeremie.rpc")
public class RpcConfiguration {

    private int serverPort;

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
