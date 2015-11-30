package com.jeremie.spring.rpc.server.nio;

import com.jeremie.spring.rpc.server.common.RpcConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

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

public class Launch implements CommandLineRunner {
    protected Logger logger = Logger.getLogger(this.getClass());

    private Executor executor = Executors.newFixedThreadPool(200);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RpcConfiguration rpcConfiguration;

    @Override
    public void run(String... args) {
        int serverPort = rpcConfiguration.getServerPort();
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            Selector selector = Selector.open();
            try {
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.socket().bind(new InetSocketAddress(serverPort));
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                logger.debug("开启nioRpc服务，端口号：" + serverPort);
                while (true) {
                    selector.select();
                    Iterator it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey selectionKey = (SelectionKey) it.next();
                        it.remove();
                        if (selectionKey.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            RpcSocket rpcSocket = new RpcSocket(socketChannel, applicationContext);
                            executor.execute(rpcSocket);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                serverSocketChannel.close();
                selector.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}