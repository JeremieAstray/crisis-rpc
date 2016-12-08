package com.jeremie.spring.rpc.remote;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.proxy.ProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.Type;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author guanhong 15/10/24 上午11:43.
 */
public abstract class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    public static Map<String, Object> lockMap = new ConcurrentHashMap<>();

    protected static long TIMEOUT = 500L;
    public static Cache<String, Object> resultCache = CacheBuilder.newBuilder().expireAfterWrite(TIMEOUT, TimeUnit.MILLISECONDS).build();
    protected boolean lazyLoading;

    public RpcClient(Boolean lazyLoading) {
        this.lazyLoading = lazyLoading;
    }

    public abstract RpcBean getRpcBean();

    public abstract void setRpcBean(RpcBean rpcBean);

    public abstract void init() throws Exception;

    public abstract Object invoke(RpcInvocation rpcInvocation) throws Exception;

    /**
     * 动态代理类
     *
     * @param rpcInvocation
     * @return
     */
    public Object dynamicProxyObject(RpcInvocation rpcInvocation) {
        lockMap.put(rpcInvocation.getClientId(), rpcInvocation);
        Class returnType = rpcInvocation.getReturnType();
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
        hancer.setSuperclass(rpcInvocation.getReturnType());
        //设置回调对象，即调用代理对象里面的方法时，实际上执行的是回调对象（里的intercept方法）。
        hancer.setCallback(new ProxyHandler() {
            @Override
            public Object intercept(Object obj, Method method, Object[] params, MethodProxy proxy) throws Throwable {
                try {
                    if (this.isFirst() && this.getObject() == null) {
                        try {
                            Object o = resultCache.getIfPresent(rpcInvocation.getClientId());
                            if (o != null)
                                this.setObject(o);
                            else {
                                synchronized (rpcInvocation) {
                                    rpcInvocation.wait(TIMEOUT);
                                }
                                o = resultCache.getIfPresent(rpcInvocation.getClientId());
                            }
                            if (o != null)
                                this.setObject(o);
                            this.setFirst(false);
                        } finally {
                            resultCache.invalidate(rpcInvocation.getClientId());
                            lockMap.remove(rpcInvocation.getClientId());
                        }
                    }
                    if (this.getObject() != null) {
                        if ("finalize".equals(method.getName())) {
                            resultCache.invalidate(rpcInvocation.getClientId());
                            this.finalize();
                            return null;
                        }
                        return method.invoke(this.getObject(), params);
                    }
                    resultCache.invalidate(rpcInvocation.getClientId());
                    return null;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    resultCache.invalidate(rpcInvocation.getClientId());
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
     * @param rpcInvocation
     * @return
     */
    public Object getObject(RpcInvocation rpcInvocation) {
        try {
            synchronized (rpcInvocation) {
                rpcInvocation.wait(TIMEOUT);
            }
            Object returnObject = resultCache.getIfPresent(rpcInvocation.getClientId());
            if (returnObject == null && rpcInvocation.getReturnType().isPrimitive())
                return this.getDefaultPrimitiveValue(Type.getType(rpcInvocation.getReturnType()).getSort());
            else
                return returnObject;
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            resultCache.invalidate(rpcInvocation.getClientId());
            lockMap.remove(rpcInvocation.getClientId());
        }
        if (rpcInvocation.getReturnType().isPrimitive())
            return this.getDefaultPrimitiveValue(Type.getType(rpcInvocation.getReturnType()).getSort());
        else
            return null;
    }


    public Object getDefaultPrimitiveValue(int sort) {
        switch (sort) {
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
                return (byte) 0;
            //SHORT
            case 4:
                return (short) 0;
            //INT
            case 5:
                return 0;
            //FLOAT
            case 6:
                return (float) 0;
            //LONG
            case 7:
                return 0L;
            //DOUBLE
            case 8:
                return (double) 0;
            default:
                return null;
        }
    }

}
