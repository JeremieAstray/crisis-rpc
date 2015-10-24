package com.jeremie.spring.rpc.nio;

import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.util.SerializeTool;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author guanhong 15/10/24 下午7:47.
 */
public class RPCNioWriteSocketThread implements Runnable {

    private Logger logger = Logger.getLogger(this.getClass());

    private Selector selector;
    private SocketChannel socketChannel;
    private RPCDto rpcDto;

    public RPCNioWriteSocketThread(Selector selector, SocketChannel socketChannel, RPCDto rpcDto) {
        this.selector = selector;
        this.socketChannel = socketChannel;
        this.rpcDto = rpcDto;
    }

    @Override
    public void run() {
        try {
            boolean written = false;
            selector.select();
            Iterator it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                it.remove();
                socketChannel = (SocketChannel) key.channel();
                if (key.isWritable() && !written) {
                    byte[] bytes = SerializeTool.objectToByteArray(rpcDto);
                    if (bytes == null)
                        throw new IllegalStateException();
                    socketChannel.write(ByteBuffer.wrap(bytes));
                    written = true;
                }
            }
        } catch (Exception e) {
            logger.error("socketchannel write error",e);
        }
    }
}
