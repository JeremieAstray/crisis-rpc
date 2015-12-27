package com.jeremie.spring.rpc.remote.http;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcHandler;
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
 * @author guanhong 15/11/18 下午4:05.
 */
public class HttpRpcThread implements Runnable {
    private Logger logger = Logger.getLogger(this.getClass());

    private String host;
    private int port;
    private RpcInvocation rpcInvocation;

    public HttpRpcThread(int port, String host, RpcInvocation rpcInvocation) {
        this.port = port;
        this.host = host;
        this.rpcInvocation = rpcInvocation;
    }

    @Override
    public void run() {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url = "http://" + host + ":" + port + "/";
            HttpPost httpPost = new HttpPost(url);
            HttpClientContext httpContext = new HttpClientContext();
            List<BasicNameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("rpcDtoStr", SerializeTool.objectToString(rpcInvocation)));
            UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            httpPost.setEntity(httpEntity);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            HttpEntity resultEntity = response.getEntity();
            //防止中文乱码
            String result = EntityUtils.toString(resultEntity, "UTF-8");
            Object object = SerializeTool.stringToObject(result);
            RpcHandler.handleMessage(object);
        } catch (IOException e) {
            logger.error("httpRequest error", e);
        }
    }
}
