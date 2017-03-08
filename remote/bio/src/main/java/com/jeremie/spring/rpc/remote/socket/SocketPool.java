package com.jeremie.spring.rpc.remote.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author guanhong 2017/2/22.
 */
public class SocketPool<T extends PoolObject> {
    private static final Logger logger = LoggerFactory.getLogger(SocketPool.class);

    private Deque<T> poolBeanLinkedListPool = new ConcurrentLinkedDeque<>();
    private PoolBeanFactory<T> poolBeanFactory;
    private Map<Integer, T> integerTMap = new ConcurrentHashMap<>();

    //private static long timeout = 180000;//ms

    private AtomicInteger size = new AtomicInteger(0);
    private static int INITIAL_SIZE = 10;
    private static int INCREASE_SIZE = 20;
    private static int MAX_SIZE = 20;
    private Lock lock = new ReentrantLock();


    public SocketPool(PoolBeanFactory<T> poolBeanFactory) {
        if (poolBeanFactory == null) {
            throw new NullPointerException("poolBeanFactory should now be null!");
        }
        this.poolBeanFactory = poolBeanFactory;
        this.newPoolBeans(INITIAL_SIZE);
    }


    private void newPoolBeans(int size) {
        while (true) {
            if (this.lock.tryLock()) {
                this.size.addAndGet(size);
                for (int i = 0; i < size; i++) {
                    T connection = this.poolBeanFactory.init();
                    this.poolBeanLinkedListPool.add(connection);
                    this.integerTMap.put(connection.getId(), connection);
                    new Thread(connection).start();
                }
                break;
            }
        }
        this.lock.unlock();
    }

    public synchronized T getConnection() {
        if (this.poolBeanLinkedListPool.isEmpty()) {
            if (this.size.get() < MAX_SIZE) {
                increasePoolSize();
            } else {
                while (this.poolBeanLinkedListPool.isEmpty()) {
                    //暂时设定等待一会
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
        //取出连接池中一个连接
        return this.poolBeanLinkedListPool.removeFirst(); // 删除第一个连接返回
    }

    //将连接放回连接池
    public void releaseConnection(int id) {
        if (this.integerTMap.get(id) != null) {
            //System.out.println("释放连接 id：" + id);
            this.poolBeanLinkedListPool.add(this.integerTMap.get(id));
        }
    }

    public void removeConnection(int id) {
        if (this.integerTMap.get(id) != null) {
            this.poolBeanLinkedListPool.remove(this.integerTMap.get(id));
            this.integerTMap.remove(id);
        }
    }

    private void increasePoolSize() {
        //System.out.println("init size : " + Math.min(MAX_SIZE - this.size.get(), INCREASE_SIZE));
        this.newPoolBeans(Math.min(MAX_SIZE - this.size.get(), INCREASE_SIZE));
    }

    public void killConnection() {
        while (true) {
            if (this.lock.tryLock()) {
                this.integerTMap.values().forEach(PoolObject::killConnection);
                break;
            }
            this.integerTMap.clear();
            this.poolBeanLinkedListPool.clear();
        }
        this.lock.unlock();
    }
}
