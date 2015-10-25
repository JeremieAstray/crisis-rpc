package com.jeremie.spring.rpc;


import com.jeremie.spring.rpc.http.HttpRPCClient;
import com.jeremie.spring.rpc.nio.RPCNioBean;
import com.jeremie.spring.rpc.nio.SocketNioRPCClient;
import com.jeremie.spring.rpc.socket.SocketBioRPCClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author guanhong 15/10/24 上午11:42.
 */
@Component
public class RPCFactory {
    private static RPCClient httpRPCClient;
    private static RPCClient socketBioRPCClient;
    private static RPCClient socketNioRPCClient;

    @Autowired
    public void setSocketNioRPCClient(SocketNioRPCClient mySocketBioRPCClient){
        socketNioRPCClient = mySocketBioRPCClient;
    }


    public static RPCClient getHttpRPCClient(){
        if (httpRPCClient==null)
            httpRPCClient = new HttpRPCClient();
        return httpRPCClient;
    }

    public static RPCClient getSocketBioRPCClient(){
        if(socketBioRPCClient == null)
            socketBioRPCClient = new SocketBioRPCClient();
        return socketBioRPCClient;
    }

    public static RPCClient getSocketNioRPCClient(){
        return socketNioRPCClient;
    }
}
