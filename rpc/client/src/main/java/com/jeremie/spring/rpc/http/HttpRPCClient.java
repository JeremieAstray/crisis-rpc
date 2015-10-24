package com.jeremie.spring.rpc.http;

import com.jeremie.spring.rpc.RPCClient;
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

    @Override
    public Object invoke(RPCDto rpcDto) {
        Object returnObject = null;
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url = "http://127.0.0.1:8081/";
            HttpPost httpPost = new HttpPost(url);
            HttpClientContext httpContext = new HttpClientContext();
            List<BasicNameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("rpcDtoStr",SerializeTool.objectToString(rpcDto)));
            UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            httpPost.setEntity(httpEntity);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            HttpEntity resultEntity = response.getEntity();
            //防止中文乱码
            String result = EntityUtils.toString(resultEntity, "UTF-8");
            Object object = SerializeTool.stringToObject(result);
            if(object instanceof RPCReceive) {
                RPCReceive rpcReceive = (RPCReceive) object;
                if (rpcReceive.getStatus() == RPCReceive.Status.SUCCESS)
                    returnObject = rpcReceive.getReturnPara();
            }
        } catch (IOException e) {
            logger.error(e);
        }
        return returnObject;
    }
}
