package com.jeremie.spring.rpc.server.common;

import com.jeremie.spring.rpc.server.util.ProxyUtil;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guanhong 15/12/4 下午2:51.
 */
//todo cache manager
public class MonitorStatus {

    public static Map<String, Map<String, MethodStatus>> clazzMethodStatusMap = new ConcurrentHashMap<>();
    public static Remote remote;
    public static List<String> remoteHostsList = new Vector<>();

    public enum Remote {
        bio, http, mina, netty, nio;

        @Override
        public String toString() {
            switch (this) {
                case bio:
                    return "bio";
                case http:
                    return "http";
                case mina:
                    return "mina";
                case netty:
                    return "netty";
                case nio:
                    return "nio";
                default:
                    return null;
            }
        }
    }

    public static void init(ApplicationContext applicationContext, Remote remote) {
        MonitorStatus.remote = remote;
        Map<String, Object> rpcServiceMap = applicationContext.getBeansWithAnnotation(Service.class);
        rpcServiceMap.forEach((beansName, bean) -> {
            Class clazz = ProxyUtil.getProxyTargetClazz(bean);
            Method[] methods = clazz.getDeclaredMethods();
            Map<String, MethodStatus> methodStatusMap = new ConcurrentHashMap<>();
            for (Method method : methods) {
                //判断非静态方法
                if (!Modifier.isStatic(method.getModifiers()))
                    //初始化方法状态Map
                    methodStatusMap.put(method.toGenericString(), new MethodStatus(method.toGenericString()));
            }
            clazzMethodStatusMap.put(clazz.getName(), methodStatusMap);
        });
    }

}
