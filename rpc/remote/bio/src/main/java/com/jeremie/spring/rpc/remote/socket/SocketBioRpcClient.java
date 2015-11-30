package com.jeremie.spring.rpc.remote.socket;


import com.jeremie.spring.rpc.dto.RpcDto;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class SocketBioRpcClient extends RpcClient {

    protected Logger logger = Logger.getLogger(this.getClass());
    private String host;
    private int port;
    private List<String> hosts;
    private Executor executor = Executors.newFixedThreadPool(200);

    public SocketBioRpcClient setHost(String host) {
        this.host = host;
        return this;
    }

    public SocketBioRpcClient setPort(int port) {
        this.port = port;
        return this;
    }

    public SocketBioRpcClient setHosts(List<String> hosts) {
        this.hosts = hosts;
        return this;
    }

    @Override
    public Object invoke(RpcDto rpcDto) {
        if(hosts!=null && !hosts.isEmpty())
            host = hosts.get(0);
        executor.execute(new SocketBioRpcThread(port,host,rpcDto));
        return dynamicProxyObject(rpcDto);
    }
}
