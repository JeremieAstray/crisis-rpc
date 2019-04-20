package com.jeremie.spring.rpc.remote.nio;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.RpcResult;
import com.jeremie.spring.rpc.remote.RpcHandler;
import com.jeremie.spring.rpc.util.SerializeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author guanhong 15/10/25 下午12:32.
 */
public class NioSocketRpcThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(NioSocketRpcThread.class);

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
                this.selector.select();
                Iterator it = this.selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();
                    this.socketChannel = (SocketChannel) key.channel();
                    if (key.isReadable()) {
                        this.dealReaderableMessage();
                    } else if (key.isWritable() && !NioRpcBean.requestQueue.isEmpty()) {
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
        if (this.socketChannel.read((ByteBuffer) byteBuffer.clear()) > 0) {
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes, 0, bytes.length);
            byteBuffer.clear();
            Object o = SerializeTool.byteArrayToObject(bytes, RpcResult.class);
            RpcHandler.handleMessage(o);
        }
    }

    public void dealWritableMessage() throws Exception {
        RpcInvocation rpcInvocation = NioRpcBean.requestQueue.poll();
        byte[] bytes = SerializeTool.objectToByteArray(rpcInvocation);
        if (bytes == null) {
            throw new IllegalStateException();
        }
        this.socketChannel.write(ByteBuffer.wrap(bytes));
    }

}
