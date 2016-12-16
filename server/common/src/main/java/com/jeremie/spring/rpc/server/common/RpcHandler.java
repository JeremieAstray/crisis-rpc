package com.jeremie.spring.rpc.server.common;

import com.jeremie.spring.rpc.RpcContext;
import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.RpcResult;
import com.jeremie.spring.rpc.server.util.ProxyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.net.SocketAddress;

/**
 * @author guanhong 15/11/24 下午3:31.
 */
public class RpcHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcHandler.class);

    private static void setRpcContext(RpcInvocation rpcInvocation) {
        RpcContext rpcContext = RpcContext.getContext();
        rpcContext.setServerName(rpcInvocation.getServerName());
        rpcContext.setArguments(rpcInvocation.getParams());
        rpcContext.setMethodName(rpcInvocation.getMethod());
        rpcContext.setParameterTypes(rpcInvocation.getParamsType());
    }

    public static void setRpcContextAddress(SocketAddress localAddress, SocketAddress remoteAddress) {
        RpcContext rpcContext = RpcContext.getContext();
        rpcContext.setLocalAddress(localAddress);
        rpcContext.setRemoteAddress(remoteAddress);
    }

    public static RpcResult handleMessage(Object message, ApplicationContext applicationContext) {
        RpcResult rpcResult = new RpcResult();
        if (message instanceof RpcInvocation) {
            RpcInvocation rpcInvocation = (RpcInvocation) message;
            setRpcContext(rpcInvocation);
            rpcResult.setServerName(rpcInvocation.getServerName());
            try {
                //获取需要被调用的class(applicationContext中)和方法
                Class clazz = Class.forName(rpcInvocation.getDestClazz());
                Object o1 = applicationContext.getBean(clazz);
                Method method = clazz.getMethod(rpcInvocation.getMethod(), rpcInvocation.getParamsType());

                //monitor日志
                Class targetClazz = ProxyUtil.getProxyTargetClazz(o1);
                Method targetMethod = targetClazz.getMethod(rpcInvocation.getMethod(), rpcInvocation.getParamsType());
                MethodStatus methodStatus = MonitorStatus.clazzMethodStatusMap.get(targetClazz.getName()).get(targetMethod.toGenericString());
                if (MonitorStatus.firstConntectTime == 0L)
                    MonitorStatus.firstConntectTime = System.currentTimeMillis();
                long begin;

                Object result;
                try {
                    //反射调用
                    begin = System.currentTimeMillis();
                    result = method.invoke(o1, rpcInvocation.getParams());
                } catch (Exception e) {

                    //异常monitor日志
                    methodStatus.addException(System.currentTimeMillis(), e);
                    methodStatus.increaseErrorCount();

                    throw e;
                }

                //monitor日志
                long invokeElapsed = System.currentTimeMillis() - begin;
                methodStatus.increaseInvokeCount();
                methodStatus.addInvokeMethodStatuses(begin, invokeElapsed);

                //返回值
                rpcResult.setReturnPara(result);
                rpcResult.setStatus(RpcResult.Status.SUCCESS);
                rpcResult.setClientId(rpcInvocation.getClientId());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                rpcResult.setClientId(rpcInvocation.getClientId());
                rpcResult.setStatus(RpcResult.Status.ERR0R);
                rpcResult.setReturnPara(null);
                rpcResult.setException(e);
            }
        } else {
            rpcResult.setReturnPara(null);
            rpcResult.setStatus(RpcResult.Status.ERR0R);
        }
        return rpcResult;
    }
}
