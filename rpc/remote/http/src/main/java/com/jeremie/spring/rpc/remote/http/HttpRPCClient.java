package com.jeremie.spring.rpc.remote.http;

import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.remote.RPCClient;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author guanhong 15/10/18 下午11:58.
 */
public class HttpRPCClient extends RPCClient {
    private Logger logger = Logger.getLogger(this.getClass());

    private String host;
    private int port;
    private List<String> hosts;
    private Executor executor = Executors.newFixedThreadPool(200);

    public HttpRPCClient setHosts(List<String> hosts) {
        this.hosts = hosts;
        return this;
    }

    public HttpRPCClient setHost(String host) {
        this.host = host;
        return this;
    }

    public HttpRPCClient setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public Object invoke(RPCDto rpcDto) {
        if (hosts != null && !hosts.isEmpty())
            host = hosts.get(0);
        executor.execute(new HttpRPCThread(port,host,rpcDto));
        return dynamicProxyObject(rpcDto);
    }
}
