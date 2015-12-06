package com.jeremie.spring.rpc.server.common;

import com.jeremie.spring.rpc.RpcContext;
import com.jeremie.spring.rpc.dto.RpcDto;
import com.jeremie.spring.rpc.dto.RpcReceive;
import com.jeremie.spring.rpc.server.util.ProxyUtil;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.net.SocketAddress;

/**
 * @author guanhong 15/11/24 下午3:31.
 */
public class RpcHandler {

    private final static Logger logger = Logger.getLogger(RpcHandler.class);

    private static void setRpcContext(RpcDto rpcDto) {
        RpcContext rpcContext = RpcContext.getContext();
        rpcContext.setArguments(rpcDto.getParams());
        rpcContext.setMethodName(rpcDto.getMethod());
        rpcContext.setParameterTypes(rpcDto.getParamsType());
    }

    public static void setRpcContextAddress(SocketAddress localAddress, SocketAddress remoteAddress) {
        RpcContext rpcContext = RpcContext.getContext();
        rpcContext.setLocalAddress(localAddress);
        rpcContext.setRemoteAddress(remoteAddress);
    }

    public static RpcReceive handleMessage(Object message, ApplicationContext applicationContext) {
        RpcReceive rpcReceive = new RpcReceive();
        if (message instanceof RpcDto) {
            RpcDto rpcDto = (RpcDto) message;
            setRpcContext(rpcDto);
            try {
                //获取需要被调用的class(applicationContext中)和方法
                Class clazz = Class.forName(rpcDto.getDestClazz());
                Object o1 = applicationContext.getBean(clazz);
                Method method = clazz.getMethod(rpcDto.getMethod(), rpcDto.getParamsType());

                //monitor日志
                Class targetClazz = ProxyUtil.getProxyTargetClazz(o1);
                Method targetMethod = targetClazz.getMethod(rpcDto.getMethod(), rpcDto.getParamsType());
                MethodStatus methodStatus = MonitorStatus.clazzMethodStatusMap.get(targetClazz.getName()).get(targetMethod.toGenericString());
                if (MonitorStatus.firstConntectTime == 0L)
                    MonitorStatus.firstConntectTime = System.currentTimeMillis();
                long begin;

                Object result;
                try {
                    //反射调用
                    begin = System.currentTimeMillis();
                    result = method.invoke(o1, rpcDto.getParams());
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
                rpcReceive.setReturnPara(result);
                rpcReceive.setStatus(RpcReceive.Status.SUCCESS);
                rpcReceive.setClientId(rpcDto.getClientId());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                rpcReceive.setClientId(rpcDto.getClientId());
                rpcReceive.setStatus(RpcReceive.Status.ERR0R);
                rpcReceive.setReturnPara(null);
                rpcReceive.setException(e);
            }
        } else {
            rpcReceive.setReturnPara(null);
            rpcReceive.setStatus(RpcReceive.Status.ERR0R);
        }
        return rpcReceive;
    }
}
