package com.jeremie.spring.rpc.nio;

import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.dto.RPCReceive;
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
public class RPCNioSocketThread implements Runnable {
    private Logger logger = Logger.getLogger(this.getClass());

    private Selector selector;
    private SocketChannel socketChannel;

    public RPCNioSocketThread(Selector selector, SocketChannel socketChannel) {
        this.selector = selector;
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            while (RPCNioBean.running) {
                selector.select();
                Iterator it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();
                    socketChannel = (SocketChannel) key.channel();
                    if (key.isReadable()) {
                        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(50 * 1024);
                        if (socketChannel.read((ByteBuffer) byteBuffer.clear()) > 0) {
                            byteBuffer.flip();
                            byte[] bytes = new byte[byteBuffer.remaining()];
                            byteBuffer.get(bytes, 0, bytes.length);
                            byteBuffer.clear();
                            Object o = SerializeTool.byteArrayToObject(bytes);
                            if (o instanceof RPCReceive) {
                                RPCReceive rpcReceive = (RPCReceive) o;
                                if (rpcReceive.getStatus() == RPCReceive.Status.SUCCESS){
                                    if(rpcReceive.getReturnPara() != null)
                                        SocketNioRPCClient.resultMap.put(rpcReceive.getClientId(),rpcReceive.getReturnPara());
                                    Thread thread = SocketNioRPCClient.threadMap.get(rpcReceive.getClientId());
                                    synchronized (thread) {
                                        thread.notify();
                                    }
                                }
                            }
                        }
                    } else if (key.isWritable() && !SocketNioRPCClient.requestQueue.isEmpty()) {
                        RPCDto rpcDto = SocketNioRPCClient.requestQueue.poll();
                        byte[] bytes = SerializeTool.objectToByteArray(rpcDto);
                        if (bytes == null)
                            throw new IllegalStateException();
                        socketChannel.write(ByteBuffer.wrap(bytes));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("socketchannel read error",e);
        }
    }
}