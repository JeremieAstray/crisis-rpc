package com.jeremie.spring.rpc.server.netty;

import com.jeremie.spring.rpc.server.common.MonitorStatus;
import com.jeremie.spring.rpc.server.common.RpcConfiguration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author guanhong 15/10/24 下午1:56.
 */
@Component
public class NettyRpcServerBean {
    private static final Logger logger = LoggerFactory.getLogger(NettyRpcServerBean.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RpcConfiguration rpcConfiguration;

    private ChannelFuture channelFuture;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void init() {
        MonitorStatus.init(this.applicationContext, MonitorStatus.Remote.netty);
        int serverPort = this.rpcConfiguration.getServerPort();
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(this.bossGroup, this.workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.softCachingConcurrentResolver(this.getClass().getClassLoader())));
                    pipeline.addLast("encoder", new ObjectEncoder());
                    // 客户端的逻辑
                    pipeline.addLast("handler", new RpcSeverHandler(applicationContext));
                }
            });
            // 服务器绑定端口监听
            this.channelFuture = serverBootstrap.bind(serverPort).sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void destroy() {
        try {
            // 监听服务器关闭监听
            if (this.channelFuture != null) {
                this.channelFuture.channel().closeFuture().sync();
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (this.bossGroup != null) {
                try {
                    this.bossGroup.shutdownGracefully().sync();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (this.workerGroup != null) {
                try {
                    this.workerGroup.shutdownGracefully().sync();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}