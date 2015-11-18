package com.jeremie.spring.rpc.protocol.nio;

import com.jeremie.spring.rpc.config.RPCClient;
import com.jeremie.spring.rpc.dto.RPCDto;
import org.apache.log4j.Logger;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author guanhong 15/10/24 下午12:47.
 */
public class SocketNioRPCClient extends RPCClient {

    protected static Queue<RPCDto> requestQueue = new ConcurrentLinkedQueue<>();
    protected Logger logger = Logger.getLogger(this.getClass());
    private Thread nioThread = null;

    private NioRPCBean nioRPCBean;

    public SocketNioRPCClient setNioRPCBean(NioRPCBean nioRPCBean) {
        this.nioRPCBean = nioRPCBean;
        return this;
    }

    @Override
    public Object invoke(RPCDto rpcDto) {
        requestQueue.add(rpcDto);
        if (!nioRPCBean.init && nioThread == null) {
            nioRPCBean.init();
            Selector selector = nioRPCBean.getSelector();
            SocketChannel socketChannel = nioRPCBean.getSocketChannel();
            nioThread = new Thread(new NioSocketRPCThread(selector, socketChannel));
            nioThread.start();
        }
        return this.dynamicProxyObject(rpcDto);
    }


}
