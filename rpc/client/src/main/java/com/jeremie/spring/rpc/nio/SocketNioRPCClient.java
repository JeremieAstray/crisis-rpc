package com.jeremie.spring.rpc.nio;

import com.jeremie.spring.rpc.RPCClient;
import com.jeremie.spring.rpc.dto.RPCDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author guanhong 15/10/24 下午12:47.
 */
@Component
public class SocketNioRPCClient implements RPCClient {

    protected Logger logger = Logger.getLogger(this.getClass());


    protected static Queue<RPCDto> requestQueue = new ConcurrentLinkedQueue<>();
    protected static Map<String, Object> resultMap = new ConcurrentHashMap<>();
    protected static Map<String, Thread> threadMap = new ConcurrentHashMap<>();
    //private static ExecutorService executor = Executors.newFixedThreadPool(100);
    Thread nioThread = null;

    @Autowired
    private RPCNioBean rpcNioBean;

    @Override
    public Object invoke(RPCDto rpcDto) {
        Thread current = Thread.currentThread();
        rpcDto.setClientId(UUID.randomUUID().toString());
        requestQueue.add(rpcDto);
        threadMap.put(rpcDto.getClientId(),current);
        if (!rpcNioBean.init && nioThread == null) {
            rpcNioBean.init();
            Selector selector = rpcNioBean.getSelector();
            SocketChannel socketChannel = rpcNioBean.getSocketChannel();
            nioThread = new Thread(new RPCNioSocketThread(selector, socketChannel));
            nioThread.start();
        }
        /*try {
            executor.execute(new RPCNioWriteSocketThread(selector,socketChannel,rpcDto));
            threadMap.put(rpcDto.getClientId(),current);
            Object o = resultMap.get(rpcDto.getClientId());
            threadMap.remove(rpcDto.getClientId());
            resultMap.remove(rpcDto.getClientId());
            return o;
        } catch (Exception e) {
            logger.error( "error",e);
        }*/
        try {
            synchronized (current) {
                current.wait(500);
            }
        } catch (InterruptedException e) {
            logger.error("error", e);
        }
        Object o = resultMap.get(rpcDto.getClientId());
        threadMap.remove(rpcDto.getClientId());
        resultMap.remove(rpcDto.getClientId());
        return o;
    }


}
