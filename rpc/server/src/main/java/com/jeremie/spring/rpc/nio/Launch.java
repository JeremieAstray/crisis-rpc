package com.jeremie.spring.rpc.nio;

import com.jeremie.spring.commons.BaseRepositoryFactoryBean;
import com.jeremie.spring.rpc.common.RPCConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author guanhong 15/10/24 下午1:56.
 */
@ComponentScan(basePackages = "${spring.ioc.componentScan.basePackages}")
@EnableJpaRepositories(basePackages = "${spring.ioc.jpaRepositories.basePackages}", repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EntityScan(basePackages = "${spring.ioc.entityScan.basePackages}")
@SpringBootApplication
@EnableEurekaClient
public class Launch implements CommandLineRunner {
    protected Logger logger = Logger.getLogger(this.getClass());

    private Executor executor = Executors.newFixedThreadPool(200);

    public static void main(String[] args) {
        SpringApplication.run(Launch.class, args);
    }

    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public void run(String... args) {
        int server_port = RPCConfiguration.SERVER_PORT;
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            Selector selector = Selector.open();
            try {
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.socket().bind(new InetSocketAddress(server_port));
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                logger.debug("开启nioRPC服务，端口号：" + server_port);
                while (true) {
                    selector.select();
                    Iterator it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey selectionKey = (SelectionKey) it.next();
                        it.remove();
                        if (selectionKey.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            RPCSocket rpcSocket = new RPCSocket(socketChannel, applicationContext);
                            executor.execute(rpcSocket);
                        }
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage(),e);
            }finally {
                serverSocketChannel.close();
                selector.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}