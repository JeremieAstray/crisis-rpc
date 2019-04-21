package com.jeremie.spring.rpc.remote;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.jeremie.spring.rpc.RpcContext;
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
import java.util.concurrent.TimeUnit;

/**
 * @author guanhong 15/10/24 上午11:43.
 */
public abstract class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    static Map<String, Object> lockMap = Maps.newConcurrentMap();
    private static Map<String, Cache<String, Object>> resultCacheMap = Maps.newConcurrentMap();
    //3s
    static long DEFAULT_TIMEOUT = 30L;
    protected boolean lazyLoading;

    public RpcClient(String serverName, Boolean lazyLoading, Long cacheTimeout) {
        this.lazyLoading = lazyLoading;
        resultCacheMap.put(serverName, CacheBuilder.newBuilder().expireAfterWrite(cacheTimeout, TimeUnit.SECONDS).build());
    }

    static Cache<String, Object> getCache(String cacheRegion) {
        return resultCacheMap.getOrDefault(cacheRegion, CacheBuilder.newBuilder().expireAfterWrite(DEFAULT_TIMEOUT, TimeUnit.SECONDS).build());
    }

    public abstract RpcBean getRpcBean();

    public abstract void setRpcBean(RpcBean rpcBean);

    public abstract void init() throws Exception;

    public abstract Object invoke(RpcInvocation rpcInvocation) throws Exception;

    /**
     * 动态代理类
     *
     * @param rpcInvocation rpcInvocation
     * @return Object
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
        ProxyHandler handler = new ProxyHandler() {
            @Override
            public Object intercept(Object obj, Method method, Object[] params, MethodProxy proxy) throws Throwable {
                try {
                    if (this.isFirst() && this.getObject() == null) {
                        try {
                            Object o = getCache(rpcInvocation.getServerName()).getIfPresent(rpcInvocation.getClientId());
                            if (o != null) {
                                this.setObject(o);
                            } else {
                                synchronized (rpcInvocation) {
                                    rpcInvocation.wait(DEFAULT_TIMEOUT);
                                }
                                o = getCache(rpcInvocation.getServerName()).getIfPresent(rpcInvocation.getClientId());
                            }
                            if (o != null) {
                                this.setObject(o);
                            }
                            this.setFirst(false);
                        } finally {
                            lockMap.remove(rpcInvocation.getClientId());
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
                    throw e;
                } finally {
                    getCache(rpcInvocation.getServerName()).invalidate(rpcInvocation.getClientId());
                }
            }
        };
        hancer.setCallback(handler);
        //future
        RpcContext.getContext().setFuture(new RpcFuture(rpcInvocation, handler));
        //创建代理对象
        return hancer.create();

        /*
        //使用future进行异步调用(其实没用到future的特性，future应该把异步处理的内容都放在这里)
        return new Future() {
            private Object object = null;
            private boolean first = true;

            private void setFirst(boolean first) {
                this.first = first;
            }

            private Object getObject() {
                return object;
            }

            private void setObject(Object object) {
                this.object = object;
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                this.object = null;
                this.first = false;
                return this.isCancelled();
            }

            @Override
            public boolean isCancelled() {
                return this.object == null;
            }

            @Override
            public boolean isDone() {
                return this.first;
            }

            @Override
            public synchronized Object get() throws InterruptedException, ExecutionException {
                return this.get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
            }

            @Override
            public synchronized Object get(long timeout, TimeUnit unit) {
                try {
                    if (this.isDone() && this.getObject() == null) {
                        Object o = getCache(rpcInvocation.getServerName()).getIfPresent(rpcInvocation.getClientId());
                        if (o != null) {
                            this.setObject(o);
                            this.setFirst(false);
                            return o;
                        } else {
                            synchronized (rpcInvocation) {
                                rpcInvocation.wait(unit.toMillis(timeout));
                            }
                            o = getCache(rpcInvocation.getServerName()).getIfPresent(rpcInvocation.getClientId());
                            if (o != null) {
                                this.setObject(o);
                                this.setFirst(false);
                                return o;
                            }
                        }
                    } else {
                        return this.getObject();
                    }
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    lockMap.remove(rpcInvocation.getClientId());
                    getCache(rpcInvocation.getServerName()).invalidate(rpcInvocation.getClientId());
                }
                return null;
            }
        };*/
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
                rpcInvocation.wait(DEFAULT_TIMEOUT);
            }
            Object returnObject = getCache(rpcInvocation.getServerName()).getIfPresent(rpcInvocation.getClientId());
            if (returnObject == null && rpcInvocation.getReturnType().isPrimitive()) {
                return this.getDefaultPrimitiveValue(Type.getType(rpcInvocation.getReturnType()).getSort());
            } else {
                return returnObject;
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            getCache(rpcInvocation.getServerName()).invalidate(rpcInvocation.getClientId());
            lockMap.remove(rpcInvocation.getClientId());
        }
        if (rpcInvocation.getReturnType().isPrimitive()) {
            return this.getDefaultPrimitiveValue(Type.getType(rpcInvocation.getReturnType()).getSort());
        } else {
            return null;
        }
    }


    private Object getDefaultPrimitiveValue(int sort) {
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
