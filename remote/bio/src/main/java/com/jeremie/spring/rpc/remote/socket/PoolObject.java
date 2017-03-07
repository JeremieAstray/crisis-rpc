package com.jeremie.spring.rpc.remote.socket;

/**
 * Created by jeremie on 2017/2/22.
 */
public interface PoolObject extends Runnable {
    int getId();
    void killConnection();
}
