package com.jeremie.spring.rpc.remote;

/**
 * Created by jeremie on 2016/11/7.
 */
public enum RpcClientEnum {

    NIO("nio", "com.jeremie.spring.rpc.remote.nio.SocketNioRpcClient", "com.jeremie.spring.rpc.remote.nio.NioRpcBean"),
    BIO("bio", "com.jeremie.spring.rpc.remote.socket.SocketBioRpcClient", ""),
    MINA("mina", "com.jeremie.spring.rpc.remote.mina.MinaRpcClient", "com.jeremie.spring.rpc.remote.mina.MinaRpcBean"),
    NETTY("netty", "com.jeremie.spring.rpc.remote.netty.NettyRpcBean", "com.jeremie.spring.rpc.remote.netty.NettyRpcBean"),
    HTTP("http", "com.jeremie.spring.rpc.remote.http.HttpRpcClient", "");

    private String name;
    private String clientClazzName;
    private String beanClazzName;

    RpcClientEnum(String name, String clientClazzName, String beanClazzName) {
        this.name = name;
        this.clientClazzName = clientClazzName;
        this.beanClazzName = beanClazzName;
    }

    public static RpcClient getRpcClientInstance(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (name == null) {
            throw new NullPointerException();
        }
        for (RpcClientEnum rpcClientEnum : RpcClientEnum.values()) {
            if (name.equals(rpcClientEnum.name)) {
                RpcClient rpcClient = (RpcClient) Class.forName(rpcClientEnum.clientClazzName).newInstance();
                if (!"".equals(rpcClientEnum.beanClazzName)) {
                    rpcClient.setRpcBean((RpcBean) Class.forName(rpcClientEnum.beanClazzName).newInstance());
                }
            }
        }
        throw new ClassNotFoundException("can not case to rpc client class name " + name + " !");
    }

}
