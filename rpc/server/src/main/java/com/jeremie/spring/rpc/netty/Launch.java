package com.jeremie.spring.rpc.netty;

import com.jeremie.spring.commons.BaseRepositoryFactoryBean;
import com.jeremie.spring.rpc.common.RPCConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
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

    public static void main(String[] args) {
        SpringApplication.run(Launch.class, args);
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) {
        int server_port = RPCConfiguration.SERVER_PORT;
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>(){

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.softCachingConcurrentResolver(this.getClass().getClassLoader())));
                    pipeline.addLast("encoder", new ObjectEncoder());
                    // 客户端的逻辑
                    pipeline.addLast("handler", new RPCSeverHandler(applicationContext));
                }
            });

            // 服务器绑定端口监听
            ChannelFuture f = b.bind(server_port).sync();
            // 监听服务器关闭监听
            f.channel().closeFuture().sync();

            // 可以简写为
            /* b.bind(portNumber).sync().channel().closeFuture().sync(); */
        }catch (InterruptedException e){
            logger.error(e.getMessage(),e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}