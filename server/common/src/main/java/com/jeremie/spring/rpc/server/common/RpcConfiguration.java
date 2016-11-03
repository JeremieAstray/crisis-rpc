package com.jeremie.spring.rpc.server.common;

//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author guanhong on 2015/11/9 16:25
 */
//@ConfigurationProperties
public class RpcConfiguration {

  //  @Value("${spring.application.name:${name:null}}")
    private String serverName;

    //@Value("${eureka.instance.non-secure-port:${serverPort:8000}}")
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
