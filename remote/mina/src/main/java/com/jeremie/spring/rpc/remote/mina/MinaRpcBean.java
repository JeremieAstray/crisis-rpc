package com.jeremie.spring.rpc.remote.mina;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @author guanhong 15/10/25 下午4:08.
 */
@Component
public class MinaRpcBean extends RpcBean {
    private static final Logger logger = LoggerFactory.getLogger(MinaRpcBean.class);

    private IoSession session;
    private IoConnector connector;
    private boolean isConnect = false;

    @Override
    public boolean isConnect() {
        return isConnect;
    }

    @Override
    public void write(RpcInvocation rpcInvocation) {
        this.session.write(rpcInvocation);
    }

    @Override
    public synchronized void init() {
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
