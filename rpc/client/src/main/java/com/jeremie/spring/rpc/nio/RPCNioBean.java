package com.jeremie.spring.rpc.nio;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author guanhong 15/10/24 下午9:24.
 */
public class RPCNioBean implements DisposableBean {

    private static Logger logger = Logger.getLogger(RPCNioBean.class);

    private static int clientPort = 8001;
    private SocketChannel socketChannel = null;
    private Selector selector = null;
    private String host = "127.0.0.1";
    private int serverPort = 8000;

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public Selector getSelector() {
        return selector;
    }

    public void init() {
        try {
            socketChannel = SocketChannel.open();
            selector = Selector.open();
            socketChannel.configureBlocking(false);
            socketChannel.bind(new InetSocketAddress(clientPort));
            socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            InetAddress addr = InetAddress.getLocalHost();
            boolean success = socketChannel.connect(new InetSocketAddress(addr, serverPort));
            if (!success) socketChannel.finishConnect();
            new Thread(new RPCNioReadSocketThread(selector, socketChannel)).start();
        } catch (Exception e) {
            logger.error("rpcNioBean init error",e);
        }
    }

    @Override
    public void destroy() throws Exception {
        try {
            if (socketChannel != null) {
                socketChannel.close();
                selector.close();
            }
        } catch (Exception e) {
            logger.error("rpcNioBean destroy error",e);
        }
    }
}
