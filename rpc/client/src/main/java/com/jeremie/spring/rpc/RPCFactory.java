package com.jeremie.spring.rpc;


import com.jeremie.spring.rpc.http.HttpRPCClient;
import com.jeremie.spring.rpc.socket.SocketBioRPCClient;

/**
 * @author guanhong 15/10/24 上午11:42.
 */
public class RPCFactory {
    private static RPCClient httpRPCClient;
    private static RPCClient socketBioRPCClient;

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
}
