package com.jeremie.spring.rpc.server.nio;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.RpcResult;
import com.jeremie.spring.rpc.server.common.MonitorStatus;
import com.jeremie.spring.rpc.server.common.RpcHandler;
import com.jeremie.spring.rpc.util.SerializeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author guanhong 15/10/24 下午10:57.
 */
public class RpcSocket implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RpcSocket.class);

    private SocketChannel socketChannel;
    private ApplicationContext applicationContext;
    private Selector selector;
    private String clientHost;

    public RpcSocket(SocketChannel socketChannel, ApplicationContext applicationContext) {
        this.socketChannel = socketChannel;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        InetSocketAddress remoteAddress = null;
        try {
            remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
            MonitorStatus.remoteHostsList.add(remoteAddress.getHostString() + ":" + remoteAddress.getPort());
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(50 * 1024);
            selector = Selector.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            this.clientHost = socketChannel.getRemoteAddress().toString();
            logger.info("与" + this.clientHost + "建立连接");
            while (true) {
                selector.select();
                Iterator it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey skey = (SelectionKey) it.next();
                    it.remove();
                    if (skey.isReadable()) {
                        socketChannel = (SocketChannel) skey.channel();
                        byteBuffer.clear();
                        socketChannel.read(byteBuffer);
                        byteBuffer.flip();
                        byte[] bytes = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bytes, 0, bytes.length);
                        byteBuffer.clear();
                        //byteBuffer.flip();
                        Object o = SerializeTool.byteArrayToObject(bytes, RpcInvocation.class);
                        RpcHandler.setRpcContextAddress(socketChannel.getLocalAddress(), socketChannel.getRemoteAddress());
                        RpcResult rpcResult = RpcHandler.handleMessage(o, applicationContext);
                        byteBuffer.clear();
                        byte[] bytes2 = SerializeTool.objectToByteArray(rpcResult);
                        if (bytes == null)
                            throw new IllegalStateException();
                        socketChannel.write(ByteBuffer.wrap(bytes2));
                    }
                }
            }
        } catch (EOFException e) {
            logger.info("与" + this.clientHost + "断开连接");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (remoteAddress != null)
                MonitorStatus.remoteHostsList.remove(remoteAddress.getHostString() + ":" + remoteAddress.getPort());
            try {
                if (socketChannel != null) {
                    socketChannel.close();
                    selector.close();
                }
            } catch (Exception e) {
                logger.error("close connect error", e);
            }
        }
    }
}
