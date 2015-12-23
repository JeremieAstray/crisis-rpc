package com.jeremie.spring.rpc.server.socket;

import com.jeremie.spring.rpc.server.common.MonitorStatus;
import com.jeremie.spring.rpc.server.common.RpcConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author guanhong 15/9/10 下午5:04.
 */

@EnableConfigurationProperties(RpcConfiguration.class)
public class Launch implements CommandLineRunner {
    protected Logger logger = Logger.getLogger(this.getClass());
    private Executor executor = Executors.newFixedThreadPool(200);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RpcConfiguration rpcConfiguration;

    @Override
    public void run(String... args) {
        MonitorStatus.init(applicationContext, MonitorStatus.Remote.bio);
        int serverPort = rpcConfiguration.getServerPort();
        logger.debug("开启Rpc服务，端口号：" + serverPort);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
            while (true) {
                Socket socket = serverSocket.accept();
                executor.execute(new RpcSocket(socket, applicationContext));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                logger.debug("关闭Rpc服务");
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
