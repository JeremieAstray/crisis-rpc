package com.jeremie.spring.rpc.remote.netty;

import com.jeremie.spring.rpc.remote.RPCBean;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.log4j.Logger;


/**
 * @author guanhong 15/10/25 下午4:08.
 */
public class NettyRPCBean extends RPCBean {
    protected Channel channel;
    private Logger logger = Logger.getLogger(this.getClass());
    private boolean isConnect = false;
    private Bootstrap bootstrap;
    private EventLoopGroup group;

    public boolean isConnect() {
        return isConnect;
    }

    public void init() {
        if (hosts != null && !hosts.isEmpty())
            host = hosts.get(0);
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
                            pipeline.addLast("handler", new NettyRPCHandler());
                        }
                    });
            // 连接服务端
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
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
