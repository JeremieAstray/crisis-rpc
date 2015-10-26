package com.jeremie.spring.rpc.nio;

import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.dto.RPCReceive;
import com.jeremie.spring.rpc.util.SerializeTool;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.io.EOFException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author guanhong 15/10/24 下午10:57.
 */
public class RPCSocket implements Runnable {

    private Logger logger = Logger.getLogger(this.getClass());

    private SocketChannel socketChannel;
    private ApplicationContext applicationContext;
    private Selector selector;
    private String clientHost;

    public RPCSocket(SocketChannel socketChannel, ApplicationContext applicationContext) {
        this.socketChannel = socketChannel;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        try {
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

                        Object o = SerializeTool.byteArrayToObject(bytes);
                        RPCReceive rpcReceive = new RPCReceive();
                        if (o instanceof RPCDto) {
                            RPCDto rpcDto = (RPCDto) o;
                            Class clazz = Class.forName(rpcDto.getDestClazz());
                            Object o1 = applicationContext.getBean(clazz);
                            Method method = clazz.getMethod(rpcDto.getMethod(), rpcDto.getParamsType());
                            Object result = method.invoke(o1, rpcDto.getParams());
                            rpcReceive.setReturnPara(result);
                            rpcReceive.setStatus(RPCReceive.Status.SUCCESS);
                            rpcReceive.setClientId(rpcDto.getClientId());
                        } else {
                            rpcReceive.setReturnPara(null);
                            rpcReceive.setStatus(RPCReceive.Status.ERR0R);
                        }
                        byteBuffer.clear();

                        byte[] bytes2 = SerializeTool.objectToByteArray(rpcReceive);
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
        }finally {
            try {
                if(socketChannel !=null){
                    socketChannel.close();
                    selector.close();
                }
            }catch (Exception e){
                logger.error("close connect error",e);
            }
        }
    }
}
