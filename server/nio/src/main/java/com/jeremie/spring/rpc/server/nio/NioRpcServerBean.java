package com.jeremie.spring.rpc.server.nio;

import com.jeremie.spring.rpc.server.common.MonitorStatus;
import com.jeremie.spring.rpc.server.common.RpcConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author guanhong 15/10/24 下午1:56.
 */
@Component
public class NioRpcServerBean {
    private static final Logger logger = LoggerFactory.getLogger(NioRpcServerBean.class);

    private Executor executor = Executors.newFixedThreadPool(200);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RpcConfiguration rpcConfiguration;

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private volatile boolean runningSignal;

    public void init() {
        MonitorStatus.init(this.applicationContext, MonitorStatus.Remote.nio);
        int serverPort = rpcConfiguration.getServerPort();
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            this.selector = Selector.open();
            this.runningSignal = true;
            try {
                this.serverSocketChannel.configureBlocking(false);
                this.serverSocketChannel.socket().bind(new InetSocketAddress(serverPort));
                this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
                logger.debug("开启nioRpc服务，端口号：" + serverPort);
                while (this.runningSignal) {
                    this.selector.select();
                    Iterator it = this.selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey selectionKey = (SelectionKey) it.next();
                        it.remove();
                        if (selectionKey.isAcceptable()) {
                            SocketChannel socketChannel = this.serverSocketChannel.accept();
                            RpcSocket rpcSocket = new RpcSocket(socketChannel, this.applicationContext);
                            executor.execute(rpcSocket);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                if (this.serverSocketChannel != null) {
                    this.serverSocketChannel.close();
                }
                if (this.selector != null) {
                    this.selector.close();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void destory() {
        try {
            if (this.runningSignal) {
                this.runningSignal = false;
                if (this.serverSocketChannel != null) {
                    this.serverSocketChannel.close();
                }
                if (this.selector != null) {
                    this.selector.close();
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}