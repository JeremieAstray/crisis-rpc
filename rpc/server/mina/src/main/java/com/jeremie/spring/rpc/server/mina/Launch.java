package com.jeremie.spring.rpc.server.mina;

import com.jeremie.spring.rpc.server.common.RpcConfiguration;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author guanhong 15/10/24 下午1:56.
 */

@EnableConfigurationProperties(RpcConfiguration.class)
public class Launch implements CommandLineRunner {
    protected Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RpcConfiguration rpcConfiguration;

    @Override
    public void run(String... args) {
        int serverPort = rpcConfiguration.getServerPort();
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter(this.getClass()));
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

        acceptor.setHandler(new RpcSeverHandler(applicationContext));
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        try {
            acceptor.bind(new InetSocketAddress(serverPort));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}