package com.jeremie.spring.rpc.remote.nio;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcHandler;
import com.jeremie.spring.rpc.util.SerializeTool;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author guanhong 15/10/25 下午12:32.
 */
public class NioSocketRpcThread implements Runnable {
    private Logger logger = Logger.getLogger(this.getClass());

    private Selector selector;
    private SocketChannel socketChannel;

    public NioSocketRpcThread(Selector selector, SocketChannel socketChannel) {
        this.selector = selector;
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            while (NioRpcBean.running) {
                selector.select();
                Iterator it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();
                    socketChannel = (SocketChannel) key.channel();
                    if (key.isReadable()) {
                        this.dealReaderableMessage();
                    } else if (key.isWritable() && !SocketNioRpcClient.requestQueue.isEmpty()) {
                        this.dealWritableMessage();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("socketchannel read error", e);
        }
    }

    public void dealReaderableMessage() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(50 * 1024);
        if (socketChannel.read((ByteBuffer) byteBuffer.clear()) > 0) {
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes, 0, bytes.length);
            byteBuffer.clear();
            Object o = SerializeTool.byteArrayToObject(bytes);
            RpcHandler.handleMessage(o);
        }
    }

    public void dealWritableMessage() throws Exception {
        RpcInvocation rpcInvocation = SocketNioRpcClient.requestQueue.poll();
        byte[] bytes = SerializeTool.objectToByteArray(rpcInvocation);
        if (bytes == null)
            throw new IllegalStateException();
        socketChannel.write(ByteBuffer.wrap(bytes));
    }

}
