package com.jeremie.spring.rpc.remote.socket;

/**
 * @author guanhong 2017/2/22.
 */
public interface PoolBeanFactory<T> {
    T init();
}
