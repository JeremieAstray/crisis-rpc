package com.jeremie.spring.rpc.protocol.socket;


import com.jeremie.spring.rpc.config.RPCClient;
import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.dto.RPCReceive;
import org.apache.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by Jeremie on 2015/5/13.
 */
public class SocketBioRPCClient implements RPCClient {

    protected Logger logger = Logger.getLogger(this.getClass());
    private Socket socket = null;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;
    private String host;
    private int port;
    private List<String> hosts;

    public SocketBioRPCClient setHost(String host) {
        this.host = host;
        return this;
    }

    public SocketBioRPCClient setPort(int port) {
        this.port = port;
        return this;
    }

    public SocketBioRPCClient setHosts(List<String> hosts) {
        this.hosts = hosts;
        return this;
    }

    @Override
    public Object invoke(RPCDto rpcDto) {
        try {
            if(hosts!=null && !hosts.isEmpty())
                host = hosts.get(0);
            socket = new Socket(host, port);
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
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return null;
    }
}
