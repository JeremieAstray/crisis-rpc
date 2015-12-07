package com.jeremie.spring.rpc.remote.http;

import com.jeremie.spring.rpc.dto.RpcReceive;
import com.jeremie.spring.rpc.server.common.MonitorStatus;
import com.jeremie.spring.rpc.server.common.RpcConfiguration;
import com.jeremie.spring.rpc.server.common.RpcHandler;
import com.jeremie.spring.rpc.util.SerializeTool;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(RpcConfiguration.class)
@ComponentScan(basePackages = {"com.jeremie.spring"})
public class Launch extends WebMvcConfigurerAdapter {
    protected Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/", produces = "text/html; charset=utf-8")
    @ResponseBody
    public String rpcServer(String rpcDtoStr, Model model, HttpServletRequest request) throws Exception {
        if(!MonitorStatus.init.get())
            MonitorStatus.init(applicationContext, MonitorStatus.Remote.http);
        MonitorStatus.remoteHostsList.add(request.getRemoteHost() + ":" + request.getRemotePort());
        Object object = SerializeTool.stringToObject(rpcDtoStr);
        RpcHandler.setRpcContextAddress(new InetSocketAddress(request.getLocalAddr(), request.getLocalPort())
                , new InetSocketAddress(request.getRemoteAddr(), request.getRemotePort()));
        RpcReceive rpcReceive = RpcHandler.handleMessage(object, applicationContext);
        MonitorStatus.remoteHostsList.remove(request.getRemoteHost() + ":" + request.getRemotePort());
        return SerializeTool.objectToString(rpcReceive);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception e) {
        logger.error(e.getMessage(), e);
        RpcReceive rpcReceive = new RpcReceive();
        rpcReceive.setReturnPara(null);
        rpcReceive.setStatus(RpcReceive.Status.ERR0R);
        return SerializeTool.objectToString(rpcReceive);
    }


}
