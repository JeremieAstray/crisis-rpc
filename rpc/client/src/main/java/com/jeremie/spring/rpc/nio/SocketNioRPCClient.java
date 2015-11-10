package com.jeremie.spring.rpc.nio;

import com.jeremie.spring.rpc.commons.RPCClient;
import com.jeremie.spring.rpc.dto.RPCDto;
import org.apache.log4j.Logger;

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
public class SocketNioRPCClient implements RPCClient {

    protected static Queue<RPCDto> requestQueue = new ConcurrentLinkedQueue<>();
    protected static Map<String, Object> resultMap = new ConcurrentHashMap<>();
    protected static Map<String, Thread> threadMap = new ConcurrentHashMap<>();
    protected Logger logger = Logger.getLogger(this.getClass());
    private Thread nioThread = null;

    private NioRPCBean nioRPCBean;

    public SocketNioRPCClient setNioRPCBean(NioRPCBean nioRPCBean) {
        this.nioRPCBean = nioRPCBean;
        return this;
    }

    @Override
    public Object invoke(RPCDto rpcDto) {
        Thread current = Thread.currentThread();
        rpcDto.setClientId(UUID.randomUUID().toString());
        requestQueue.add(rpcDto);
        threadMap.put(rpcDto.getClientId(), current);
        if (!nioRPCBean.init && nioThread == null) {
            nioRPCBean.init();
            Selector selector = nioRPCBean.getSelector();
            SocketChannel socketChannel = nioRPCBean.getSocketChannel();
            nioThread = new Thread(new RPCNioSocketThread(selector, socketChannel));
            nioThread.start();
        }
        try {
            synchronized (current) {
                current.wait(500);
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        Object o = resultMap.get(rpcDto.getClientId());
        threadMap.remove(rpcDto.getClientId());
        resultMap.remove(rpcDto.getClientId());
        return o;
    }


}
