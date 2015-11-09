package com.jeremie.spring.rpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;


/**
 * @author guanhong 15/10/25 下午4:08.
 */
public class NettyRPCBean implements DisposableBean {
    private static Logger logger = Logger.getLogger(NettyRPCBean.class);

    private String host = "127.0.0.1";
    private int serverPort = 8000;
    private boolean isConnect = false;
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    protected Channel channel;

    public NettyRPCBean(List<String> hosts) {
        if(hosts!=null && !hosts.isEmpty())
            host = hosts.get(0);
    }
    public boolean isConnect() {
        return isConnect;
    }

    public void init() {
        group = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.softCachingConcurrentResolver(this.getClass().getClassLoader())));
                            pipeline.addLast("encoder", new ObjectEncoder());
                            // 客户端的逻辑
                            pipeline.addLast("handler", new RPCClientHandler());
                        }
                    });
            // 连接服务端
            channel = bootstrap.connect(host, serverPort).sync().channel();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(),e);
        }
        isConnect = true;
    }

    @Override
    public void destroy() throws Exception {
        //关闭
        if (bootstrap != null) {
            group.shutdownGracefully();
        }
    }
}
