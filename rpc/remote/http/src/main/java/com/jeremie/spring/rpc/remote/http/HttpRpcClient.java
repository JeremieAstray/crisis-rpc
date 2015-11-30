package com.jeremie.spring.rpc.remote.http;

import com.jeremie.spring.rpc.dto.RpcDto;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author guanhong 15/10/18 下午11:58.
 */
public class HttpRpcClient extends RpcClient {
    private Logger logger = Logger.getLogger(this.getClass());

    private String host;
    private int port;
    private List<String> hosts;
    private Executor executor = Executors.newFixedThreadPool(200);

    public HttpRpcClient setHosts(List<String> hosts) {
        this.hosts = hosts;
        return this;
    }

    public HttpRpcClient setHost(String host) {
        this.host = host;
        return this;
    }

    public HttpRpcClient setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public Object invoke(RpcDto rpcDto) {
        if (hosts != null && !hosts.isEmpty())
            host = hosts.get(0);
        executor.execute(new HttpRpcThread(port,host,rpcDto));
        return dynamicProxyObject(rpcDto);
    }
}
