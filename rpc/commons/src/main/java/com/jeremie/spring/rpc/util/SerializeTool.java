package com.jeremie.spring.rpc.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author guanhong 15/10/23 下午3:56.
 */
public class SerializeTool {

    private static Logger logger = Logger.getLogger(SerializeTool.class);

    public static String objectToString(Object object){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            String objectString = byteArrayOutputStream.toString("ISO-8859-1");
            objectOutputStream.close();
            byteArrayOutputStream.close();
            return objectString;
        }catch (IOException e){
            logger.error(e);
        }
        return null;
    }

    public static Object stringToObject(String string){
        try {
            byte[] objectBytes = string.getBytes("ISO-8859-1");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objectBytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object o = objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
            return o;
        }catch (IOException | ClassNotFoundException e){
            logger.error(e);
        }
        return null;
    }
}
