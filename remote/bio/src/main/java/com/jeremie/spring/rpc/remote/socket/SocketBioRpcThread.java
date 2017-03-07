package com.jeremie.spring.rpc.remote.socket;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author guanhong 15/11/18 下午4:13.
 */
public class SocketBioRpcThread implements Runnable, PoolObject {
    private static final Logger logger = LoggerFactory.getLogger(SocketBioRpcThread.class);

    private static AtomicInteger ID = new AtomicInteger(0);

    private int id;
    private volatile RpcInvocation rpcInvocation;
    private Socket socket = null;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;
    private volatile boolean running = true;
    private Thread currentThread;

    public SocketBioRpcThread(int port, String host) {
        this.id = ID.incrementAndGet();
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public synchronized void handleObject(RpcInvocation rpcInvocation) {
        this.rpcInvocation = rpcInvocation;
        this.currentThread.notify();
    }

    @Override
    public void run() {
        try {
            this.currentThread = Thread.currentThread();
            while (this.running) {
                this.currentThread.wait();
                if (this.rpcInvocation != null) {
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(rpcInvocation);
                    Object o = objectInputStream.readObject();
                    RpcHandler.handleMessage(o);
                    rpcInvocation = null;
                    SocketBioRpcBean.socketBioRpcThreadSocketPool.releaseConnection(this.getId());
                }
            }
        } catch (EOFException e) {
            logger.debug("socket连接结束");
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
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

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void killConnection() {
        this.running = false;
        this.currentThread.notify();
    }
}
