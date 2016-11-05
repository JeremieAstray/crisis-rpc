package com.jeremie.spring.rpc.remote.http;

import com.jeremie.spring.rpc.RpcResult;
import com.jeremie.spring.rpc.server.common.MonitorStatus;
import com.jeremie.spring.rpc.server.common.RpcHandler;
import com.jeremie.spring.rpc.util.SerializeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import java.net.InetSocketAddress;

/**
 * @author guanhong 15/9/10 下午5:04.
 */
@Controller
@ComponentScan(basePackages = {"com.jeremie.spring"})
public class Launch extends WebMvcConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(Launch.class);

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/", produces = "text/html; charset=utf-8")
    @ResponseBody
    public String rpcServer(String rpcDtoStr, Model model, HttpServletRequest request) throws Exception {
        if (!MonitorStatus.init.get())
            MonitorStatus.init(applicationContext, MonitorStatus.Remote.http);
        MonitorStatus.remoteHostsList.add(request.getRemoteHost() + ":" + request.getRemotePort());
        Object object = SerializeTool.stringToObject(rpcDtoStr);
        RpcHandler.setRpcContextAddress(new InetSocketAddress(request.getLocalAddr(), request.getLocalPort())
                , new InetSocketAddress(request.getRemoteAddr(), request.getRemotePort()));
        RpcResult rpcResult = RpcHandler.handleMessage(object, applicationContext);
        MonitorStatus.remoteHostsList.remove(request.getRemoteHost() + ":" + request.getRemotePort());
        return SerializeTool.objectToString(rpcResult);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception e) {
        logger.error(e.getMessage(), e);
        RpcResult rpcResult = new RpcResult();
        rpcResult.setReturnPara(null);
        rpcResult.setStatus(RpcResult.Status.ERR0R);
        return SerializeTool.objectToString(rpcResult);
    }


}
