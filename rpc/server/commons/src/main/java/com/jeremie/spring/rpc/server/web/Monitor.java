package com.jeremie.spring.rpc.server.web;

import com.jeremie.spring.rpc.server.common.RpcConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guanhong 15/12/4 下午2:26.
 */
@RestController
@RequestMapping("/rpcMonitor")
public abstract class Monitor {

    @Autowired
    private RpcConfiguration rpcConfiguration;
    @Autowired
    private ServerProperties serverProperties;
    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/connectorCount", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getConnectorCount() {
        applicationContext.getBeansWithAnnotation(Service.class);
        return null;
    }

    @RequestMapping(value = "/rpcConfig", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getRpcConfig(){
        return null;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getStatus() {
        return null;
    }

    @RequestMapping(value = "/allMethodStatus", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getAllMethodStatus(){
        return null;
    }

    @RequestMapping(value = "/methodStatus", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public String getOneMethodStatus(@RequestParam(value = "method") String method){
        return null;
    }

    @RequestMapping(value = "/rpcStatus", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getAllStatus(){
        return null;
    }


}

