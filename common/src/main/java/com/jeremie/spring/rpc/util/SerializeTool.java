package com.jeremie.spring.rpc.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author guanhong 15/10/23 下午3:56.
 */
public class SerializeTool {

    private static final Logger logger = LoggerFactory.getLogger(SerializeTool.class);

    public static String objectToString(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            return byteArrayOutputStream.toString("ISO-8859-1");
        } catch (IOException e) {
            logger.error("objectToString error", e);
        } finally {
            try {
                if (objectOutputStream != null)
                    objectOutputStream.close();
                if (byteArrayOutputStream != null)
                    byteArrayOutputStream.close();
            } catch (IOException e) {
                logger.error("close stream error", e);
            }
        }
        return null;
    }

    public static Object stringToObject(String string) {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byte[] objectBytes = string.getBytes("ISO-8859-1");
            byteArrayInputStream = new ByteArrayInputStream(objectBytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("stringToObject error", e);
        } finally {
            try {
                if (objectInputStream != null)
                    objectInputStream.close();
                if (byteArrayInputStream != null)
                    byteArrayInputStream.close();
            } catch (IOException e) {
                logger.error("close stream error", e);
            }
        }
        return null;
    }

    public static Object byteArrayToObject(byte[] byteArray) throws EOFException {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(byteArray);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        } catch (EOFException e) {
            throw e;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("byteArrayToObject error", e);
        } finally {
            try {
                if (objectInputStream != null)
                    objectInputStream.close();
                if (byteArrayInputStream != null)
                    byteArrayInputStream.close();

            } catch (IOException e) {
                logger.error("close stream error", e);
            }
        }
        return null;
    }

    public static byte[] objectToByteArray(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("objectToByteArray error", e);
        } finally {
            try {
                if (objectOutputStream != null)
                    objectOutputStream.close();
                if (byteArrayOutputStream != null)
                    byteArrayOutputStream.close();
            } catch (IOException e) {
                logger.error("close stream error", e);
            }
        }
        return null;
    }
}
