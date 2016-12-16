package com.jeremie.spring.rpc.remote;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by jeremie on 2016/11/7.
 */
public enum RpcClientEnum {

    NIO("nio", "com.jeremie.spring.rpc.remote.nio.SocketNioRpcClient", "com.jeremie.spring.rpc.remote.nio.NioRpcBean"),
    BIO("bio", "com.jeremie.spring.rpc.remote.socket.SocketBioRpcClient", "com.jeremie.spring.rpc.remote.socket.SocketBioRpcBean"),
    MINA("mina", "com.jeremie.spring.rpc.remote.mina.MinaRpcClient", "com.jeremie.spring.rpc.remote.mina.MinaRpcBean"),
    NETTY("netty", "com.jeremie.spring.rpc.remote.netty.NettyRpcBean", "com.jeremie.spring.rpc.remote.netty.NettyRpcBean"),
    HTTP("http", "com.jeremie.spring.rpc.remote.http.HttpRpcClient", "com.jeremie.spring.rpc.remote.http.HttpRpcBean");

    private String name;
    private String clientClazzName;
    private String beanClazzName;

    RpcClientEnum(String name, String clientClazzName, String beanClazzName) {
        this.name = name;
        this.clientClazzName = clientClazzName;
        this.beanClazzName = beanClazzName;
    }

    public static RpcClient getRpcClientInstance(String serverName, String clientName, boolean lazyLoading, Long cacheTimeout) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        if (clientName == null) {
            throw new NullPointerException();
        }
        for (RpcClientEnum rpcClientEnum : RpcClientEnum.values()) {
            if (clientName.equals(rpcClientEnum.name)) {
                RpcClient rpcClient = (RpcClient) Class.forName(rpcClientEnum.clientClazzName).getDeclaredConstructor(String.class, Boolean.class, Long.class).newInstance(serverName, lazyLoading, cacheTimeout);
                if (!"".equals(rpcClientEnum.beanClazzName)) {
                    rpcClient.setRpcBean((RpcBean) Class.forName(rpcClientEnum.beanClazzName).newInstance());
                }
                return rpcClient;
            }
        }
        throw new ClassNotFoundException("can not case to rpc client class name " + clientName + " !");
    }

}
