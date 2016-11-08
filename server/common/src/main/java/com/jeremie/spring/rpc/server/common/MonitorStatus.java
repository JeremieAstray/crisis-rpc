package com.jeremie.spring.rpc.server.common;

import com.jeremie.spring.rpc.server.util.ProxyUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author guanhong 15/12/4 下午2:51.
 */
//todo cache manager
public class MonitorStatus {

    public static Map<String, Map<String, MethodStatus>> clazzMethodStatusMap = new ConcurrentHashMap<>();
    public static Long firstConntectTime = 0L;
    public static Remote remote;
    public static List<String> remoteHostsList = new Vector<>();

    public static AtomicBoolean init = new AtomicBoolean(false);

    public static void init(ApplicationContext applicationContext, Remote remote) {
        MonitorStatus.remote = remote;
        Map<String, Object> rpcServiceMap = applicationContext.getBeansWithAnnotation(Service.class);
        if (rpcServiceMap != null && !rpcServiceMap.isEmpty()) {
            rpcServiceMap.forEach((beansName, bean) -> {
                Class clazz = ProxyUtil.getProxyTargetClazz(bean);
                if (clazz != null) {
                    Method[] methods = clazz.getDeclaredMethods();
                    Map<String, MethodStatus> methodStatusMap = new ConcurrentHashMap<>();
                    for (Method method : methods) {
                        //判断非静态方法
                        if (!Modifier.isStatic(method.getModifiers()))
                            //初始化方法状态Map
                            methodStatusMap.put(method.toGenericString(), new MethodStatus(method.toGenericString()));
                    }
                    clazzMethodStatusMap.put(clazz.getName(), methodStatusMap);
                }
            });
        }
        init.set(true);
    }

    public enum Remote {
        bio("bio"), http("http"), mina("mina"), netty("netty"), nio("nio");

        private final String name;

        Remote(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

}
