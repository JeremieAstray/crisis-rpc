package com.jeremie.spring.rpc.server.mina;

import com.jeremie.spring.rpc.server.common.MonitorStatus;
import com.jeremie.spring.rpc.server.common.RpcConfiguration;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author guanhong 15/10/24 下午1:56.
 */
@Component
public class MinaRpcServerBean {
    private static final Logger logger = LoggerFactory.getLogger(MinaRpcServerBean.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RpcConfiguration rpcConfiguration;

    private IoAcceptor minaIoAcceptor;

    public void init() {
        MonitorStatus.init(this.applicationContext, MonitorStatus.Remote.mina);
        int serverPort = this.rpcConfiguration.getServerPort();
        this.minaIoAcceptor = new NioSocketAcceptor();
        this.minaIoAcceptor.getFilterChain().addLast("logger", new LoggingFilter(this.getClass()));
        this.minaIoAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

        this.minaIoAcceptor.setHandler(new RpcSeverHandler(this.applicationContext));
        this.minaIoAcceptor.getSessionConfig().setReadBufferSize(2048);
        this.minaIoAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        try {
            this.minaIoAcceptor.bind(new InetSocketAddress(serverPort));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void destroy() {
        if (this.minaIoAcceptor != null) {
            this.minaIoAcceptor.dispose();
        }
    }


}