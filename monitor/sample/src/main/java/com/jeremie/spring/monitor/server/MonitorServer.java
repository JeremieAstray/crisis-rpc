package com.jeremie.spring.monitor.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guanhong 15/12/8 下午2:16.
 */
@RestController
public class MonitorServer {
    @Autowired
    private EurekaClient discoverEurekaClient;

    private static final Logger logger = LoggerFactory.getLogger(MonitorServer.class);

    @RequestMapping(path = "/monitor")
    public String status() {
        List<Application> applicationList = discoverEurekaClient.getApplications().getRegisteredApplications();
        List<InstanceInfo> instanceInfoList = new ArrayList<>();
        JSONObject result = new JSONObject();
        applicationList.forEach(application -> {
            String applicationName = application.getName();
            application.getInstances().forEach(instanceInfoList::add);
            if (!result.containsKey(applicationName))
                result.put(applicationName, new JSONArray());
        });
        instanceInfoList.forEach(instanceInfo -> {
            try {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                String url = "http://" + instanceInfo.getHostName() + ":" + instanceInfo.getPort() + "/rpcMonitor/rpcStatus";
                HttpGet httpGet = new HttpGet(url);
                HttpClientContext httpContext = new HttpClientContext();
                HttpResponse response = httpClient.execute(httpGet, httpContext);
                HttpEntity resultEntity = response.getEntity();
                String re = EntityUtils.toString(resultEntity);
                JSONObject jsonObject = JSONObject.parseObject(re);
                jsonObject.put("host", instanceInfo.getIPAddr());
                JSONArray jsonArray = (JSONArray) result.get(instanceInfo.getAppName());
                jsonArray.add(jsonObject);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        });
        return result.toJSONString();
    }
}
