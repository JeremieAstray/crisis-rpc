package com.jeremie.spring.rpc.remote.socket;


import com.jeremie.spring.rpc.RpcContext;
import com.jeremie.spring.rpc.RpcHandler;
import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.dto.RPCReceive;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Created by jeremie on 2015/6/4.
 */
public class RPCSocket implements Runnable {

    protected Logger logger = Logger.getLogger(this.getClass());
    public ObjectOutputStream objectOutputStream = null;
    public ObjectInputStream objectInputStream = null;
    private Socket socket;
    private ApplicationContext applicationContext;

    public RPCSocket(Socket sockek, ApplicationContext applicationContext) {
        this.socket = sockek;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        try {
            objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            Object o = objectInputStream.readObject();
            RpcHandler.setRPCContextAddress(socket.getLocalSocketAddress(),socket.getRemoteSocketAddress());
            RPCReceive rpcReceive = RpcHandler.handleMessage(o,applicationContext);
            objectOutputStream.writeObject(rpcReceive);

        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage(),e);
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.flush();
                    objectOutputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }
            try {
                if (objectInputStream != null)
                    objectInputStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
            try {
                if (!socket.isClosed())
                    socket.getInputStream().close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
            try {
                logger.debug(socket.getInetAddress() + " close!");
                socket.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }

        }
    }
}
