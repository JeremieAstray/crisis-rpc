package com.jeremie.spring.rpc.remote.nio;


import com.jeremie.spring.rpc.remote.RpcBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author guanhong 15/10/24 下午9:24.
 */
@Component
public class NioRpcBean extends RpcBean {

    protected static boolean running = false;
    protected boolean init = false;
    private static final Logger logger = LoggerFactory.getLogger(NioRpcBean.class);
    private SocketChannel socketChannel = null;
    private Selector selector = null;

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public Selector getSelector() {
        return selector;
    }

    @Override
    public void init() {
        super.init();
        try {
            socketChannel = SocketChannel.open();
            selector = Selector.open();
            socketChannel.configureBlocking(false);
            socketChannel.bind(new InetSocketAddress(clientPort));
            socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            boolean success = socketChannel.connect(new InetSocketAddress(host, port));
            if (!success) socketChannel.finishConnect();
            running = true;
            init = true;
        } catch (Exception e) {
            logger.error("rpcNioBean init error", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        try {
            running = false;
            init = false;
            if (socketChannel != null) {
                socketChannel.close();
                selector.close();
            }
        } catch (Exception e) {
            logger.error("rpcNioBean destroy error", e);
        }
    }
}
