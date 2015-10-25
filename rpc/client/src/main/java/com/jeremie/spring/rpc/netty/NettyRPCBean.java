package com.jeremie.spring.rpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;

import java.net.InetSocketAddress;

/**
 * @author guanhong 15/10/25 下午4:08.
 */
public class NettyRPCBean implements DisposableBean {
    private static Logger logger = Logger.getLogger(NettyRPCBean.class);

    private String host = "127.0.0.1";
    private int serverPort = 8000;
    private boolean isConnect = false;
    private Bootstrap bootstrap;
    protected ChannelFuture channelFuture;
    private EventLoopGroup group;
    public boolean isConnect() {
        return isConnect;
    }

    public void init() {
        group = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new ObjectEncoder(), new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new RPCClientHandler());
                        }
                    });

            // Start the client.
            channelFuture = bootstrap.connect(host, serverPort).sync();
        }catch (InterruptedException e){
            logger.error("error",e);
        }
        isConnect = true;
    }

    @Override
    public void destroy() throws Exception {
        //关闭
        if(bootstrap!=null){
            channelFuture.channel().close();
            group.shutdownGracefully();
        }
    }
}
