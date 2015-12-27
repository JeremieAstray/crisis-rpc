package com.jeremie.spring.rpc.remote.proxy;


import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * @author guanhong 15/11/15 下午4:21.
 */
public abstract class ProxyHandler implements MethodInterceptor {

    private Object object = null;
    private boolean first = true;

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public abstract Object intercept(Object obj, java.lang.reflect.Method method, Object[] args,
                                     MethodProxy proxy) throws Throwable;

    @Override
    public void finalize() throws Throwable {
        super.finalize();
    }
}
