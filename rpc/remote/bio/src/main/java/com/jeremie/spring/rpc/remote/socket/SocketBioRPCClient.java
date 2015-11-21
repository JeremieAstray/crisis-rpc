package com.jeremie.spring.rpc.remote.socket;


import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.remote.RPCClient;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class SocketBioRPCClient extends RPCClient {

    protected Logger logger = Logger.getLogger(this.getClass());
    private String host;
    private int port;
    private List<String> hosts;
    private Executor executor = Executors.newFixedThreadPool(200);

    public SocketBioRPCClient setHost(String host) {
        this.host = host;
        return this;
    }

    public SocketBioRPCClient setPort(int port) {
        this.port = port;
        return this;
    }

    public SocketBioRPCClient setHosts(List<String> hosts) {
        this.hosts = hosts;
        return this;
    }

    @Override
    public Object invoke(RPCDto rpcDto) {
        if(hosts!=null && !hosts.isEmpty())
            host = hosts.get(0);
        executor.execute(new SocketBioRPCThread(port,host,rpcDto));
        return dynamicProxyObject(rpcDto);
    }
}
