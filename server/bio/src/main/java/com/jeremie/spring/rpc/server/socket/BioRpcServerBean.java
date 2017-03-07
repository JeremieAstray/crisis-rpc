package com.jeremie.spring.rpc.server.socket;

import com.jeremie.spring.rpc.server.common.MonitorStatus;
import com.jeremie.spring.rpc.server.common.RpcConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author guanhong 15/9/10 下午5:04.
 */
@Component
public class BioRpcServerBean {
    private static final Logger logger = LoggerFactory.getLogger(BioRpcServerBean.class);
    private Executor executor = Executors.newFixedThreadPool(200);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RpcConfiguration rpcConfiguration;

    private volatile boolean runningSignal;
    private ServerSocket serverSocket;

    public void init() {
        MonitorStatus.init(this.applicationContext, MonitorStatus.Remote.bio);
        int serverPort = this.rpcConfiguration.getServerPort();
        logger.debug("开启BioRpc服务，端口号：" + serverPort);
        try {
            this.serverSocket = new ServerSocket(serverPort);
            this.runningSignal = true;
            while (this.runningSignal) {
                Socket socket = this.serverSocket.accept();
                this.executor.execute(new RpcSocket(socket, this.applicationContext));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                logger.debug("关闭BioRpc服务");
                if (this.serverSocket != null) {
                    this.serverSocket.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void destroy() {
        try {
            if (this.runningSignal && this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
