package com.jeremie.spring.rpc.remote;

import com.jeremie.spring.rpc.dto.RpcDto;
import com.jeremie.spring.rpc.remote.proxy.ProxyHandler;
import org.apache.log4j.Logger;
import org.springframework.asm.Type;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guanhong 15/10/24 上午11:43.
 */
public abstract class RpcClient {
    public static Map<String, Object> resultMap = new ConcurrentHashMap<>();
    public static Map<String, Object> lockMap = new ConcurrentHashMap<>();
    Logger logger = Logger.getLogger(this.getClass());

    public abstract Object invoke(RpcDto rpcDto);


    /**
     * 动态代理类
     *
     * @param rpcDto
     * @return
     */
    public Object dynamicProxyObject(RpcDto rpcDto) {
        lockMap.put(rpcDto.getClientId(), rpcDto);
        Class returnType = rpcDto.getReturnType();
        try {
            if (Void.TYPE.equals(returnType)
                    || TypeUtils.isStatic(returnType.getModifiers())
                    || TypeUtils.isFinal(returnType.getModifiers())
                    || TypeUtils.isPrimitive(TypeUtils.getType(returnType.getName()))) {
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        //代理返回对象
        Enhancer hancer = new Enhancer();
        //设置代理对象的父类
        hancer.setSuperclass(rpcDto.getReturnType());
        //设置回调对象，即调用代理对象里面的方法时，实际上执行的是回调对象（里的intercept方法）。
        hancer.setCallback(new ProxyHandler() {
            @Override
            public Object intercept(Object obj, Method method, Object[] params, MethodProxy proxy) throws Throwable {
                try {
                    if (this.isFirst() && this.getObject() == null) {
                        try {
                            Object o = resultMap.get(rpcDto.getClientId());
                            if (o != null)
                                this.setObject(o);
                            else {
                                synchronized (rpcDto) {
                                    rpcDto.wait(500);
                                }
                                o = resultMap.get(rpcDto.getClientId());
                            }
                            if (o != null)
                                this.setObject(o);
                            this.setFirst(false);
                        } finally {
                            resultMap.remove(rpcDto.getClientId());
                            lockMap.remove(rpcDto.getClientId());
                        }
                    }
                    if (this.getObject() != null) {
                        if ("finalize".equals(method.getName())) {
                            this.finalize();
                            return null;
                        }
                        return method.invoke(this.getObject(), params);
                    }
                    return null;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return null;
                }
            }
        });
        //创建代理对象
        return hancer.create();
    }

    /**
     * 当不能生成代理对象时,使用同步获取rpcDto的方式
     *
     * @param rpcDto
     * @return
     */
    public Object getObject(RpcDto rpcDto) {
        try {
            synchronized (rpcDto) {
                rpcDto.wait(500);
            }
            Object returnObject = resultMap.get(rpcDto.getClientId());
            if (returnObject == null && rpcDto.getReturnType().isPrimitive())
                return this.getDefaultPrimitiveValue(Type.getType(rpcDto.getReturnType()).getSort());
            else
                return returnObject;
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            resultMap.remove(rpcDto.getClientId());
            lockMap.remove(rpcDto.getClientId());
        }
        if (rpcDto.getReturnType().isPrimitive())
            return this.getDefaultPrimitiveValue(Type.getType(rpcDto.getReturnType()).getSort());
        else
            return null;
    }


    public Object getDefaultPrimitiveValue(int sort){
        switch (sort){
            //VOID
            case 0:
                return null;
            //BOOLEAN
            case 1:
                return false;
            //CHAR
            case 2:
                return ' ';
            //BYTE
            case 3:
                return (byte)0;
            //SHORT
            case 4:
                return (short)0;
            //INT
            case 5:
                return 0;
            //FLOAT
            case 6:
                return (float)0;
            //LONG
            case 7:
                return 0L;
            //DOUBLE
            case 8:
                return (double)0;
            default:
                return null;
        }
    }

}
