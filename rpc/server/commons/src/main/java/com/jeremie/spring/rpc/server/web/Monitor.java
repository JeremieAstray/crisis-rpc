package com.jeremie.spring.rpc.server.web;

import com.alibaba.fastjson.JSON;
import com.jeremie.spring.rpc.server.common.MethodStatus;
import com.jeremie.spring.rpc.server.common.MonitorStatus;
import com.jeremie.spring.rpc.server.common.RpcConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author guanhong 15/12/4 下午2:26.
 */
@RestController
@RequestMapping("/rpcMonitor")
public class Monitor {

    @Autowired
    private RpcConfiguration rpcConfiguration;
    @Autowired
    private ServerProperties serverProperties;

    @RequestMapping(value = "/connectorCount", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getConnectorCount() {
        Map<String, Integer> map = new HashMap<>();
        map.put("connectCount", MonitorStatus.remoteHostsList.size());
        return JSON.toJSONString(map);
    }

    @RequestMapping(value = "/remoteHostList", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getRemoteHostList() {
        Map<String, Object> map = new HashMap<>();
        map.put("hostList", MonitorStatus.remoteHostsList);
        return JSON.toJSONString(map);
    }

    @RequestMapping(value = "/rpcConfig", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getRpcConfig() {
        Map<String, Object> map = new HashMap<>();
        map.put("rpcMethod", MonitorStatus.remote);
        map.put("rpcPort", rpcConfiguration.getServerPort());
        map.put("rpcServerName", rpcConfiguration.getServerName());
        return JSON.toJSONString(map);
    }

    @RequestMapping(value = "/allClazzStatus", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getAllMethodStatus() {
        return JSON.toJSONString(MonitorStatus.clazzMethodStatusMap);
    }

    @RequestMapping(value = "/clazzStatus", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    public String getOneclazzStatus(@RequestParam(value = "clazz") String clazz) {
        Map<String, MethodStatus> methodStatusMap = MonitorStatus.clazzMethodStatusMap.get(clazz);
        return JSON.toJSONString(methodStatusMap);
    }

    @RequestMapping(value = "/rpcStatus", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
    public String getAllStatus() {
        Map<String, Object> map = new HashMap<>();
        map.put("connectCount", MonitorStatus.remoteHostsList.size());
        map.put("hostList", MonitorStatus.remoteHostsList);
        map.put("clazzMethodStatus", MonitorStatus.clazzMethodStatusMap);
        map.put("rpcMethod", MonitorStatus.remote);
        map.put("rpcPort", rpcConfiguration.getServerPort());
        map.put("rpcServerName", rpcConfiguration.getServerName());
        return JSON.toJSONString(map);
    }


}

