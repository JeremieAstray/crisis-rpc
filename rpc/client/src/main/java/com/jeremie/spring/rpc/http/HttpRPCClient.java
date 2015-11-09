package com.jeremie.spring.rpc.http;

import com.jeremie.spring.rpc.RPCClient;
import com.jeremie.spring.rpc.commons.RPCConfiguration;
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
    protected Logger logger = Logger.getLogger(this.getClass());

    private String host = "127.0.0.1";
    private int port = 8081;

    @Override
    public Object invoke(RPCDto rpcDto) {
        List<String> hosts = RPCConfiguration.getHosts();
        if(hosts!=null && !hosts.isEmpty())
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
