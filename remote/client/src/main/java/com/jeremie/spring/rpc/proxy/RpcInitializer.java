package com.jeremie.spring.rpc.proxy;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.config.RpcConfiguration;
import com.jeremie.spring.rpc.config.ServiceConfig;
import com.jeremie.spring.rpc.remote.RpcBean;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @author guanhong 15/10/17 下午11:40.
 */

public class RpcInitializer {

    private final String RESOURCE_PATTERN = "/**/*.class";
    private static final Logger logger = LoggerFactory.getLogger(RpcInitializer.class);
    private ConfigurableApplicationContext applicationContext;
    private Map<String, RpcClient> rpcClientMap;
    private List<RpcBean> rpcBeanList;
    private RpcConfiguration rpcConfiguration;
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setRpcBeanList(List<RpcBean> rpcBeanList) {
        this.rpcBeanList = rpcBeanList;
    }

    public void setRpcClientMap(Map<String, RpcClient> rpcClientMap) {
        this.rpcClientMap = rpcClientMap;
    }

    public void setRpcConfiguration(RpcConfiguration rpcConfiguration) {
        this.rpcConfiguration = rpcConfiguration;
    }

    public void init() {
        ConfigurableBeanFactory beanFactory = applicationContext.getBeanFactory();
        Map<String, List<Class>> clazzMap;
        try {
            clazzMap = getClassMap();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("getClassSet error", e);
            return;
        }
        clazzMap.forEach((serviceName, clazzList) -> clazzList.forEach(clazz -> {
            boolean isInterface = clazz.isInterface();
            if (isInterface) {
                //jdk方案 代理服务
                Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (proxy, method, params) -> {
                    RpcInvocation rpcInvocation = new RpcInvocation();
                    rpcInvocation.setClientId(UUID.randomUUID().toString());
                    rpcInvocation.setDestClazz(clazz.getName());
                    rpcInvocation.setParams(params);
                    rpcInvocation.setMethod(method.getName());
                    rpcInvocation.setParamsType(method.getParameterTypes());
                    rpcInvocation.setReturnType(method.getReturnType());
                    return rpcClientMap.get(serviceName).invoke(rpcInvocation);
                });

                //cglib方案
                /*Enhancer hancer = new Enhancer();
                hancer.setInterfaces(new Class[]{clazz});
                hancer.setCallback((InvocationHandler) (o, method, params) -> {
                    RpcInvocation rpcDto = new RpcInvocation();
                    rpcDto.setClientId(UUID.randomUUID().toString());
                    rpcDto.setDestClazz(clazz.getName());
                    rpcDto.setParams(params);
                    rpcDto.setMethod(method.getName());
                    rpcDto.setParamsType(method.getParameterTypes());
                    rpcDto.setReturnType(method.getReturnType());
                    return rpcClientMap.get(serviceName).invoke(rpcDto);
                });
                Object o = hancer.create();*/
                beanFactory.registerSingleton(clazz.getSimpleName(), o);
            }
        }));
    }

    /**
     * 获取代理class集合
     *
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Map<String, List<Class>> getClassMap() throws IOException, ClassNotFoundException {
        Map<String, List<Class>> clazzMap = new HashMap<>();
        clazzMap.clear();
        for (ServiceConfig serviceConfig : rpcConfiguration.getServices()) {
            List<String> packages = serviceConfig.getPackages();
            if (!packages.isEmpty()) {
                for (String pkg : packages) {
                    String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                            ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
                    Resource[] resources = resourcePatternResolver.getResources(pattern);
                    MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                    for (Resource resource : resources) {
                        if (resource.isReadable()) {
                            MetadataReader reader = readerFactory.getMetadataReader(resource);
                            String className = reader.getClassMetadata().getClassName();
                            clazzMap.putIfAbsent(serviceConfig.getName(), new ArrayList<>());
                            clazzMap.get(serviceConfig.getName()).add(Class.forName(className));
                        }
                    }
                }
            }
        }
        //输出日志
        if (logger.isInfoEnabled()) {
            for (Map.Entry<String, List<Class>> clazzEntry : clazzMap.entrySet()) {
                clazzEntry.getValue().forEach(clazz -> logger.info(String.format("Found rpc Services:%s's class:%s", clazzEntry.getKey(), clazz.getName())));
            }
        }
        return clazzMap;
    }

    public void destroy() throws Exception {
        for (RpcBean rpcBean : rpcBeanList)
            rpcBean.destroy();
    }
}