package com.jeremie.spring.rpc.protocol.socket;

import com.jeremie.spring.commons.BaseRepositoryFactoryBean;
import com.jeremie.spring.rpc.common.RPCConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author guanhong 15/9/10 下午5:04.
 */
@ComponentScan(basePackages = "${spring.ioc.componentScan.basePackages}")
@EnableJpaRepositories(basePackages = "${spring.ioc.jpaRepositories.basePackages}",repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EntityScan(basePackages = "${spring.ioc.entityScan.basePackages}")
@SpringBootApplication
@EnableEurekaClient
@EnableConfigurationProperties(RPCConfiguration.class)
public class Launch implements CommandLineRunner {
    protected Logger logger = Logger.getLogger(this.getClass());
    private Executor executor = Executors.newFixedThreadPool(200);
    public static void main(String[] args) {
        SpringApplication.run(Launch.class, args);
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RPCConfiguration rpcConfiguration;

    @Override
    public void run(String... args) {
        int serverPort = rpcConfiguration.getServerPort();
        logger.debug("开启RPC服务，端口号：" + serverPort);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
            while (true) {
                Socket socket = serverSocket.accept();
                executor.execute(new RPCSocket(socket,applicationContext));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } finally {
            try {
                logger.debug("关闭RPC服务");
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }
}
