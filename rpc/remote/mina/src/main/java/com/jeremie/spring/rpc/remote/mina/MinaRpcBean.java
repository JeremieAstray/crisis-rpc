package com.jeremie.spring.rpc.remote.mina;

import com.jeremie.spring.rpc.remote.RpcBean;
import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

/**
 * @author guanhong 15/10/25 下午4:08.
 */
public class MinaRpcBean extends RpcBean {
    private Logger logger = Logger.getLogger(this.getClass());

    private IoSession session;
    private IoConnector connector;
    private boolean isConnect = false;

    public IoSession getSession() {
        return session;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void init() throws Exception {
        if (hosts != null && !hosts.isEmpty())
            host = hosts.get(0);
        connector = new NioSocketConnector();
        connector.getFilterChain().addLast("logger", new LoggingFilter(this.getClass()));
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        connector.setHandler(new MinaRpcHandler());
        ConnectFuture connectFuture = connector.connect(new InetSocketAddress(host, port));
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
