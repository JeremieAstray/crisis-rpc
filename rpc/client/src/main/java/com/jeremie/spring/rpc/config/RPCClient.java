package com.jeremie.spring.rpc.config;

import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.proxy.ProxyHandler;
import org.apache.log4j.Logger;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author guanhong 15/10/24 上午11:43.
 */
public abstract class RPCClient {
    Logger logger = Logger.getLogger(this.getClass());

    public abstract Object invoke(RPCDto rpcDto);

    public static Map<String, Object> resultMap = new ConcurrentHashMap<>();
    public static Map<String, Lock> lockMap = new ConcurrentHashMap<>();

    /**
     * 动态代理类
     *
     * @param rpcDto
     * @return
     */
    public Object dynamicProxyObject(RPCDto rpcDto) {
        Lock lock = new ReentrantLock(true);
        lockMap.put(rpcDto.getClientId(),lock);
        Class returnType = rpcDto.getReturnType();
        try {
            if ("void".equals(returnType.getSimpleName())) {
                resultMap.remove(rpcDto.getClientId());
                lockMap.remove(rpcDto.getClientId());
                return null;
            }else if (returnType.isArray()
                    || TypeUtils.isFinal(returnType.getModifiers())
                    || TypeUtils.isPrimitive(TypeUtils.getType(returnType.getName()))) {
                Object o = null;
                try {
                    lock.tryLock(5000,TimeUnit.MILLISECONDS);
                    o = resultMap.get(rpcDto.getClientId());
                }finally {
                    resultMap.remove(rpcDto.getClientId());
                    lockMap.remove(rpcDto.getClientId());
                }
                return o;
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
                                lock.tryLock(5000,TimeUnit.MILLISECONDS);
                                o = resultMap.get(rpcDto.getClientId());
                            }
                            if (o != null)
                                this.setObject(o);
                            this.setFirst(false);
                        }finally {
                            resultMap.remove(rpcDto.getClientId());
                            lockMap.remove(rpcDto.getClientId());
                        }
                    }
                    if (this.getObject() != null)
                        return method.invoke(this.getObject(), params);
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
}
