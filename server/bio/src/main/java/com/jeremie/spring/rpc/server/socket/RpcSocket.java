package com.jeremie.spring.rpc.server.socket;


import com.jeremie.spring.rpc.RpcEndSignal;
import com.jeremie.spring.rpc.RpcResult;
import com.jeremie.spring.rpc.server.common.MonitorStatus;
import com.jeremie.spring.rpc.server.common.RpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by jeremie on 2015/6/4.
 */
public class RpcSocket implements Runnable {

    public ObjectOutputStream objectOutputStream = null;
    public ObjectInputStream objectInputStream = null;
    private static final Logger logger = LoggerFactory.getLogger(RpcSocket.class);
    private Socket socket;
    private ApplicationContext applicationContext;
    private volatile boolean running = true;

    public RpcSocket(Socket sockek, ApplicationContext applicationContext) {
        this.socket = sockek;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        InetSocketAddress remoteAddress = null;
        try {
            remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
            MonitorStatus.remoteHostsList.add(remoteAddress.getHostString() + ":" + remoteAddress.getPort());
            objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            while (this.running) {
                Object o = objectInputStream.readObject();
                if (o instanceof RpcEndSignal) {
                    objectOutputStream.writeObject(new RpcEndSignal());
                    objectOutputStream.flush();
                    this.closeThread();
                } else {
                    RpcHandler.setRpcContextAddress(socket.getLocalSocketAddress(), socket.getRemoteSocketAddress());
                    RpcResult rpcResult = RpcHandler.handleMessage(o, applicationContext);
                    objectOutputStream.writeObject(rpcResult);
                    objectOutputStream.flush();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (remoteAddress != null) {
                MonitorStatus.remoteHostsList.remove(remoteAddress.getHostString() + ":" + remoteAddress.getPort());
            }
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
                if (!socket.isClosed())
                    socket.getInputStream().close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            try {
                logger.debug(socket.getInetAddress() + " close!");
                socket.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

        }
    }

    public void closeThread() {
        this.running = false;
    }
}
