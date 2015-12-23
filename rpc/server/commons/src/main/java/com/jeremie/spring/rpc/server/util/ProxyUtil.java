package com.jeremie.spring.rpc.server.util;

import org.apache.log4j.Logger;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;

import java.lang.reflect.Field;

/**
 * @author guanhong 15/12/5 下午4:03.
 */
public class ProxyUtil {
    private static Logger logger = Logger.getLogger(ProxyUtil.class);

    public static Class getProxyTargetClazz(Object bean) {
        try {
            Field h = bean.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            AopProxy aopProxy = (AopProxy) h.get(bean);

            Field advised = aopProxy.getClass().getDeclaredField("advised");
            advised.setAccessible(true);

            Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();

            return target.getClass();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
