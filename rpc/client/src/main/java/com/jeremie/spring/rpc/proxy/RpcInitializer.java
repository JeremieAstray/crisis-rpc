package com.jeremie.spring.rpc.proxy;

import com.jeremie.spring.rpc.config.RPCClient;
import com.jeremie.spring.rpc.dto.RPCDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author guanhong 15/10/17 下午11:40.
 */
@Component
public class RpcInitializer {

    private final String RESOURCE_PATTERN = "/**/*.class";
    protected Logger logger = Logger.getLogger(RpcInitializer.class);
    private static RPCClient rpcClient;

    @Autowired
    private void setRpcClient(RPCClient rpcClient) {
        RpcInitializer.rpcClient = rpcClient;
    }

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private List<String> packagesList = new ArrayList<>();

    public void setPackagesList(List<String> packagesList) {
        this.packagesList = packagesList;
    }

    public void rpcInit(ConfigurableApplicationContext applicationContext) {
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
            Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (proxy, method, params) -> {
                RPCDto rpcDto = new RPCDto();
                rpcDto.setDestClazz(clazz.getName());
                rpcDto.setParams(params);
                rpcDto.setMethod(method.getName());
                rpcDto.setParamsType(method.getParameterTypes());
                rpcDto.setReturnType(method.getReturnType());
                return rpcClient.invoke(rpcDto);
            });
            beanFactory.registerSingleton(clazz.getSimpleName(), o);
        });
    }

    public Set<Class> getClassSet() throws IOException, ClassNotFoundException {
        Set<Class> classSet = new HashSet<>();
        classSet.clear();
        List<String> packages = this.packagesList;
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
