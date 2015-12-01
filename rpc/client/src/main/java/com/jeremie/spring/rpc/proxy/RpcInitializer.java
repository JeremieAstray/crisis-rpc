package com.jeremie.spring.rpc.proxy;

import com.jeremie.spring.rpc.config.RpcConfiguration;
import com.jeremie.spring.rpc.config.ServiceConfig;
import com.jeremie.spring.rpc.dto.RpcDto;
import com.jeremie.spring.rpc.remote.RpcBean;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author guanhong 15/10/17 下午11:40.
 */

public class RpcInitializer implements DisposableBean {

    private ConfigurableApplicationContext applicationContext;
    private Map<String, RpcClient> rpcClientMap;
    private List<RpcBean> rpcBeanList;
    private RpcConfiguration rpcConfiguration;

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

    private final String RESOURCE_PATTERN = "/**/*.class";
    protected Logger logger = Logger.getLogger(RpcInitializer.class);
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public void init() {
        ConfigurableBeanFactory beanFactory = applicationContext.getBeanFactory();
        Map<String, Class> clazzMap;
        try {
            clazzMap = getClassMap();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("getClassSet error", e);
            return;
        }
        clazzMap.forEach((serviceName, clazz) -> {
            boolean isInterface = clazz.isInterface();
            if (isInterface) {
                //jdk方案 代理服务
                Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (proxy, method, params) -> {
                    RpcDto rpcDto = new RpcDto();
                    rpcDto.setClientId(UUID.randomUUID().toString());
                    rpcDto.setDestClazz(clazz.getName());
                    rpcDto.setParams(params);
                    rpcDto.setMethod(method.getName());
                    rpcDto.setParamsType(method.getParameterTypes());
                    rpcDto.setReturnType(method.getReturnType());
                    return rpcClientMap.get(serviceName).invoke(rpcDto);
                });
                /*
                //cglib方案
                Enhancer hancer = new Enhancer();
                hancer.setInterfaces(new Class[]{clazz});
                hancer.setCallback((InvocationHandler) (o, method, params) -> {
                    RpcDto rpcDto = new RpcDto();
                    rpcDto.setClientId(UUID.randomUUID().toString());
                    rpcDto.setDestClazz(clazz.getName());
                    rpcDto.setParams(params);
                    rpcDto.setMethod(method.getName());
                    rpcDto.setParamsType(method.getParameterTypes());
                    rpcDto.setReturnType(method.getReturnType());
                    return rpcClientMap.get(serviceName).invoke(rpcDto);
                });*/
                beanFactory.registerSingleton(clazz.getSimpleName(), o);
            }
        });
    }

    /**
     * 获取代理class集合
     *
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Map<String, Class> getClassMap() throws IOException, ClassNotFoundException {
        Map<String, Class> clazzMap = new HashMap<>();
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
                            clazzMap.put(serviceConfig.getName(), Class.forName(className));
                        }
                    }
                }
            }
        }
        //输出日志
        if (logger.isInfoEnabled()) {
            for (Map.Entry<String, Class> clazzEntry : clazzMap.entrySet()) {
                logger.info(String.format("Found rpc Services:%s's class:%s", clazzEntry.getKey(), clazzEntry.getValue().getName()));
            }
        }
        return clazzMap;
    }

    @Override
    public void destroy() throws Exception {
        for (RpcBean rpcBean : rpcBeanList)
            rpcBean.destroy();
    }
}
