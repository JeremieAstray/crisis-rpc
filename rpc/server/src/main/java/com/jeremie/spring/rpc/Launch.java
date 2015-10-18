package com.jeremie.spring.rpc;

import com.jeremie.spring.commons.BaseRepositoryFactoryBean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author guanhong 15/9/10 下午5:04.
 */
@ComponentScan(basePackages = "${spring.ioc.componentScan.basePackages}")
@EnableJpaRepositories(basePackages = "${spring.ioc.jpaRepositories.basePackages}",repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EntityScan(basePackages = "${spring.ioc.entityScan.basePackages}")
@SpringBootApplication
public class Launch implements CommandLineRunner {
    protected Logger logger = Logger.getLogger(Launch.class);
    public static void main(String[] args) {
        SpringApplication.run(Launch.class, args);
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        logger.debug("开启RPC服务，端口号：" + 8000);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8000);
            while (true) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new RPCSokcet(socket,applicationContext));
                thread.start();
            }
        } catch (IOException e) {
            logger.error(e);
        } finally {
            try {
                logger.debug("关闭RPC服务");
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
}
