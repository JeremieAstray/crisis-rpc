package com.jeremie.spring.rpc.protocol.socket;

import com.jeremie.spring.rpc.config.RPCHandler;
import com.jeremie.spring.rpc.dto.RPCDto;
import org.apache.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author guanhong 15/11/18 下午4:13.
 */
public class SocketBioRPCThread implements Runnable {
    private Logger logger = Logger.getLogger(this.getClass());

    private String host;
    private int port;
    private RPCDto rpcDto;
    private Socket socket = null;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;

    public SocketBioRPCThread(int port, String host, RPCDto rpcDto) {
        this.port = port;
        this.host = host;
        this.rpcDto = rpcDto;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcDto);
            Object o = objectInputStream.readObject();
            RPCHandler.handleMessage(o);
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
