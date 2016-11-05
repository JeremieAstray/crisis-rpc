package com.jeremie.spring.rpc.server.common;

/**
 * @author guanhong on 2015/11/9 16:25
 */
public class RpcConfiguration {

    private String serverName;

    private int serverPort;

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
