package com.jeremie.spring.rpc.remote.nio;

import com.jeremie.spring.rpc.dto.RpcDto;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.apache.log4j.Logger;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author guanhong 15/10/24 下午12:47.
 */
public class SocketNioRpcClient extends RpcClient {

    protected static Queue<RpcDto> requestQueue = new ConcurrentLinkedQueue<>();
    protected Logger logger = Logger.getLogger(this.getClass());
    private Thread nioThread = null;

    private NioRpcBean nioRpcBean;

    public SocketNioRpcClient setNioRpcBean(NioRpcBean nioRpcBean) {
        this.nioRpcBean = nioRpcBean;
        return this;
    }

    @Override
    public Object invoke(RpcDto rpcDto) {
        requestQueue.add(rpcDto);
        if (!nioRpcBean.init && nioThread == null) {
            nioRpcBean.init();
            Selector selector = nioRpcBean.getSelector();
            SocketChannel socketChannel = nioRpcBean.getSocketChannel();
            nioThread = new Thread(new NioSocketRpcThread(selector, socketChannel));
            nioThread.start();
        }
        return this.dynamicProxyObject(rpcDto);
    }


}
