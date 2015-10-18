package com.jeremie.spring.rpc;


import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.dto.RPCReceive;
import org.apache.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class RPCClient {

    protected Logger logger = Logger.getLogger(this.getClass());
    private Socket socket = null;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;

    public Object invoke(RPCDto rpcDto) {
        try {
            socket = new Socket("127.0.0.1", 8000);
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcDto);
            Object o = objectInputStream.readObject();
            if (o instanceof RPCReceive) {
                RPCReceive rpcReceive = (RPCReceive) o;
                if (rpcReceive.getStatus() == RPCReceive.Status.SUCCESS)
                    return rpcReceive.getReturnPara();
                else
                    return null;
            }
        } catch (EOFException e) {
            logger.debug("socket连接结束");
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e);
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.flush();
                    objectOutputStream.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
            try {
                if (objectInputStream != null)
                    objectInputStream.close();
            } catch (IOException e) {
                logger.error(e);
            }
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                logger.error(e);
            }
        }
        return null;
    }
}
