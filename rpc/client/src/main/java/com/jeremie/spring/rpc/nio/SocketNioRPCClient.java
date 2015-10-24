package com.jeremie.spring.rpc.nio;

import com.jeremie.spring.rpc.RPCClient;
import com.jeremie.spring.rpc.dto.RPCDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author guanhong 15/10/24 下午12:47.
 */
public class SocketNioRPCClient implements RPCClient {

    protected Logger logger = Logger.getLogger(this.getClass());


    protected static Map<String,RPCDto> requestMap = new ConcurrentHashMap<>();
    protected static Map<String,Object> resultMap = new ConcurrentHashMap<>();
    protected static Map<String,Thread> threadMap = new ConcurrentHashMap<>();
    private static ExecutorService executor = Executors.newFixedThreadPool(100);

    private RPCNioBean rpcNioBean;

    public SocketNioRPCClient(RPCNioBean rpcNioBean) {
        this.rpcNioBean = rpcNioBean;
    }

    @Override
    public Object invoke(RPCDto rpcDto) {
        Thread current = Thread.currentThread();
        rpcDto.setClientId(UUID.randomUUID().toString());
        Selector selector = rpcNioBean.getSelector();
        SocketChannel socketChannel = rpcNioBean.getSocketChannel();
        try {
            executor.execute(new RPCNioWriteSocketThread(selector,socketChannel,rpcDto));
            threadMap.put(rpcDto.getClientId(),current);
            synchronized (current) {
                current.wait();
            }
            Object o = resultMap.get(rpcDto.getClientId());
            threadMap.remove(rpcDto.getClientId());
            resultMap.remove(rpcDto.getClientId());
            return o;
        } catch (Exception e) {
            logger.error( "error",e);
        }
        return null;
    }


}
