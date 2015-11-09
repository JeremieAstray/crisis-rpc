package com.jeremie.spring.rpc.nio;


import com.jeremie.spring.rpc.commons.RPCConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * @author guanhong 15/10/24 下午9:24.
 */
public class RPCNioBean implements DisposableBean {

    private static Logger logger = Logger.getLogger(RPCNioBean.class);

    private static int clientPort = RPCConfiguration.DEFAULT_NIO_CLIENT_PORT;
    private SocketChannel socketChannel = null;
    private Selector selector = null;
    private String host = RPCConfiguration.DEFAULT_IP;
    private int port = RPCConfiguration.DEFAULT_PORT;
    protected static boolean running = false;
    protected boolean init = false;

    public RPCNioBean(List<String> hosts) {
        if(hosts!=null && !hosts.isEmpty())
            host = hosts.get(0);
    }
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
            boolean success = socketChannel.connect(new InetSocketAddress(host, port));
            if (!success) socketChannel.finishConnect();
            running = true;
            init = true;
        } catch (Exception e) {
            logger.error("rpcNioBean init error",e);
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
            logger.error("rpcNioBean destroy error",e);
        }
    }
}
