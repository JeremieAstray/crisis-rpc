package com.jeremie.spring.rpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
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
    public boolean isConnect() {
        return isConnect;
    }

    public void init() {
        // Client服务启动器 3.x的ClientBootstrap 改为Bootstrap，且构造函数变化很大，这里用无参构造。
        bootstrap = new Bootstrap();
        // 指定channel类型
        bootstrap.channel(NioSocketChannel.class);
        // 指定Handler
        bootstrap.handler(new RPCClientHandler());
        // 指定EventLoopGroup
        bootstrap.group(new NioEventLoopGroup());
        // 连接到服务端
        channelFuture = bootstrap.connect( new InetSocketAddress(host , serverPort));
        isConnect = true;
    }

    @Override
    public void destroy() throws Exception {
        //关闭
        if(bootstrap!=null){
            channelFuture.cancel(true);
        }
    }
}
