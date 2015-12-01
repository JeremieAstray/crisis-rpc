package com.jeremie.spring.rpc.proxy;

import com.jeremie.spring.rpc.config.RpcConfiguration;
import com.jeremie.spring.rpc.dto.RpcDto;
import com.jeremie.spring.rpc.remote.RpcClient;
import org.apache.log4j.Logger;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author guanhong 15/10/17 下午11:40.
 */

public class RpcInitializer {

    private ConfigurableApplicationContext applicationContext;
    private static RpcClient rpcClient;
    private RpcConfiguration rpcConfiguration;

    public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setRpcClient(RpcClient rpcClient) {
        RpcInitializer.rpcClient = rpcClient;
    }

    public void setRpcConfiguration(RpcConfiguration rpcConfiguration) {
        this.rpcConfiguration = rpcConfiguration;
    }

    private final String RESOURCE_PATTERN = "/**/*.class";
    protected Logger logger = Logger.getLogger(RpcInitializer.class);
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public void init() {
        ConfigurableBeanFactory beanFactory = applicationContext.getBeanFactory();
        Set<Class> clazzs = null;
        try {
            clazzs = getClassSet();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("getClassSet error", e);
            return;
        }
        clazzs.forEach(clazz -> {
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
                    return rpcClient.invoke(rpcDto);
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
                    return rpcClient.invoke(rpcDto);
                });*/
                beanFactory.registerSingleton(clazz.getSimpleName(), o);
            }
        });
    }

    public Set<Class> getClassSet() throws IOException, ClassNotFoundException {
        Set<Class> classSet = new HashSet<>();
        classSet.clear();
        List<String> packages = rpcConfiguration.getServices().get(0).getPackages();
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
                        classSet.add(Class.forName(className));
                    }
                }
            }
        }
        //输出日志
        if (logger.isInfoEnabled()) {
            for (Class<?> clazz : classSet) {
                logger.info(String.format("Found class:%s", clazz.getName()));
            }
        }
        return classSet;
    }
}
