package com.jeremie.spring.rpc.mina;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.beans.factory.DisposableBean;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author guanhong 15/10/25 下午4:08.
 */
public class MinaRPCBean implements DisposableBean {
    private static Logger logger = Logger.getLogger(MinaRPCBean.class);
    private String host = "127.0.0.1";
    private int serverPort = 8000;
    private IoSession session;
    private IoConnector connector;
    private boolean isConnect = false;

    public MinaRPCBean(List<String> hosts) {
        if(hosts!=null && !hosts.isEmpty())
            host = hosts.get(0);
    }

    public IoSession getSession() {
        return session;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void init() throws Exception{
        connector = new NioSocketConnector();
        connector.getFilterChain().addLast("logger", new LoggingFilter(this.getClass()));
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        connector.setHandler(new RPCClientHandler());
        ConnectFuture connectFuture = connector.connect(new InetSocketAddress(host, serverPort));
        //等待建立连接
        connectFuture.awaitUninterruptibly();
        session = connectFuture.getSession();
        isConnect = true;
    }

    @Override
    public void destroy() {
        try {
            //关闭
            if (session != null) {
                if (session.isConnected()) {
                    session.getCloseFuture().awaitUninterruptibly();
                }
                connector.dispose(true);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
