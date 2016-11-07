package com.jeremie.spring.rpc.remote.nio;


import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author guanhong 15/10/24 下午9:24.
 */
public class NioRpcBean extends RpcBean {
    static Queue<RpcInvocation> requestQueue = new ConcurrentLinkedQueue<>();

    static boolean running = false;
    private boolean init = false;
    private static final Logger logger = LoggerFactory.getLogger(NioRpcBean.class);
    private SocketChannel socketChannel = null;
    private Selector selector = null;
    private Thread nioThread = null;

    @Override
    public void write(RpcInvocation rpcInvocation) {
        requestQueue.add(rpcInvocation);
        if (!this.init && this.nioThread == null) {
            this.init();
            this.nioThread = new Thread(new NioSocketRpcThread(this.selector, this.socketChannel));
            this.nioThread.start();
        }
    }

    @Override
    public void init() {
        try {
            this.socketChannel = SocketChannel.open();
            this.selector = Selector.open();
            this.socketChannel.configureBlocking(false);
            this.socketChannel.bind(new InetSocketAddress(this.clientPort));
            this.socketChannel.register(this.selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            boolean success = this.socketChannel.connect(new InetSocketAddress(host, port));
            if (!success) {
                socketChannel.finishConnect();
            }
            running = true;
            this.init = true;
        } catch (Exception e) {
            logger.error("rpcNioBean init error", e);
        }
    }

    @Override
    public boolean isConnect() {
        return false;
    }

    @Override
    public void destroy() throws Exception {
        try {
            running = false;
            this.init = false;
            if (this.socketChannel != null) {
                this.socketChannel.close();
            }
            if (this.selector != null) {
                this.selector.close();
            }
        } catch (Exception e) {
            logger.error("rpcNioBean destroy error", e);
        }
    }
}
