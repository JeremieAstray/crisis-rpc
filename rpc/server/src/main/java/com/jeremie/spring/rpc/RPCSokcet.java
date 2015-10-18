package com.jeremie.spring.rpc;


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
public class RPCSokcet implements Runnable {

    protected Logger logger = Logger.getLogger(this.getClass());
    public ObjectOutputStream objectOutputStream = null;
    public ObjectInputStream objectInputStream = null;
    private Socket socket;
    private ApplicationContext applicationContext;

    public RPCSokcet(Socket sockek,ApplicationContext applicationContext) {
        this.socket = sockek;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        try {
            objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            objectInputStream = new ObjectInputStream(this.socket.getInputStream());

            Object o = objectInputStream.readObject();
            if (o instanceof RPCDto){
                RPCDto rpcDto = (RPCDto) o;
                Class clazz = Class.forName(rpcDto.getDestClazz());
                Object o1 = applicationContext.getBean(clazz);
                Method method = clazz.getMethod(((RPCDto) o).getMethod(), ((RPCDto) o).getParamsType());
                Object result = method.invoke(o1,((RPCDto) o).getParams());
                RPCReceive rpcReceive = new RPCReceive();
                rpcReceive.setReturnPara(result);
                rpcReceive.setStatus(RPCReceive.Status.SUCCESS);
                objectOutputStream.writeObject(rpcReceive);
            }else{
                RPCReceive rpcReceive = new RPCReceive();
                rpcReceive.setReturnPara(null);
                rpcReceive.setStatus(RPCReceive.Status.ERR0R);
                objectOutputStream.writeObject(rpcReceive);
            }
        } catch (IOException |InvocationTargetException | IllegalAccessException |NoSuchMethodException| ClassNotFoundException e) {
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
                if (!socket.isClosed())
                    socket.getInputStream().close();
            } catch (IOException e) {
                logger.error(e);
            }
            try {
                logger.debug(socket.getInetAddress() + " close!");
                socket.close();
            } catch (IOException e) {
                logger.error(e);
            }

        }
    }
}
