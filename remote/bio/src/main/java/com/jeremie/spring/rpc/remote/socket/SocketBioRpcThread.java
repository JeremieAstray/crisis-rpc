package com.jeremie.spring.rpc.remote.socket;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcHandler;
import org.apache.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author guanhong 15/11/18 下午4:13.
 */
public class SocketBioRpcThread implements Runnable {
    private Logger logger = Logger.getLogger(this.getClass());

    private String host;
    private int port;
    private RpcInvocation rpcInvocation;
    private Socket socket = null;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;

    public SocketBioRpcThread(int port, String host, RpcInvocation rpcInvocation) {
        this.port = port;
        this.host = host;
        this.rpcInvocation = rpcInvocation;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcInvocation);
            Object o = objectInputStream.readObject();
            RpcHandler.handleMessage(o);
        } catch (EOFException e) {
            logger.debug("socket连接结束");
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.flush();
                    objectOutputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            try {
                if (objectInputStream != null)
                    objectInputStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
