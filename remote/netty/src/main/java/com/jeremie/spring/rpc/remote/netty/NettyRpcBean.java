package com.jeremie.spring.rpc.remote.netty;

import com.jeremie.spring.rpc.RpcInvocation;
import com.jeremie.spring.rpc.remote.RpcBean;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author guanhong 15/10/25 下午4:08.
 */
public class NettyRpcBean extends RpcBean {
    private Channel channel;
    private static final Logger logger = LoggerFactory.getLogger(NettyRpcBean.class);
    private boolean isConnect = false;
    private Bootstrap bootstrap;
    private EventLoopGroup group;

    @Override
    public boolean isConnect() {
        return this.isConnect;
    }

    @Override
    public void write(RpcInvocation rpcInvocation) {
        this.channel.writeAndFlush(rpcInvocation);
    }

    @Override
    public void init() throws Exception {
        this.group = new NioEventLoopGroup();
        try {
            this.bootstrap = new Bootstrap();
            this.bootstrap.group(this.group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.softCachingConcurrentResolver(this.getClass().getClassLoader())));
                            pipeline.addLast("encoder", new ObjectEncoder());
                            // 客户端的逻辑
                            pipeline.addLast("handler", new NettyRpcHandler());
                        }
                    });
            // 连接服务端
            this.channel = this.bootstrap.connect(this.host, this.port).sync().channel();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        this.isConnect = true;
    }

    @Override
    public void destroy() throws Exception {
        //关闭
        if (this.bootstrap != null) {
            this.group.shutdownGracefully();
        }
    }
}
