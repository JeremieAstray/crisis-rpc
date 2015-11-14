package com.jeremie.spring.rpc.protocol.http;

import com.jeremie.spring.rpc.config.RPCClient;
import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.dto.RPCReceive;
import com.jeremie.spring.rpc.util.SerializeTool;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guanhong 15/10/18 下午11:58.
 */
public class HttpRPCClient implements RPCClient {
    private Logger logger = Logger.getLogger(this.getClass());

    private String host;
    private int port;
    private List<String> hosts;

    public HttpRPCClient setHosts(List<String> hosts) {
        this.hosts = hosts;
        return this;
    }

    public HttpRPCClient setHost(String host) {
        this.host = host;
        return this;
    }

    public HttpRPCClient setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public Object invoke(RPCDto rpcDto) {
        if (hosts != null && !hosts.isEmpty())
            host = hosts.get(0);
        Object returnObject = null;
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url = "http://" + host + ":" + port + "/";
            HttpPost httpPost = new HttpPost(url);
            HttpClientContext httpContext = new HttpClientContext();
            List<BasicNameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("rpcDtoStr", SerializeTool.objectToString(rpcDto)));
            UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            httpPost.setEntity(httpEntity);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            HttpEntity resultEntity = response.getEntity();
            //防止中文乱码
            String result = EntityUtils.toString(resultEntity, "UTF-8");
            Object object = SerializeTool.stringToObject(result);
            if (object instanceof RPCReceive) {
                RPCReceive rpcReceive = (RPCReceive) object;
                if (rpcReceive.getStatus() == RPCReceive.Status.SUCCESS)
                    returnObject = rpcReceive.getReturnPara();
            }
        } catch (IOException e) {
            logger.error("httpRequest error", e);
        }
        return returnObject;
    }
}
