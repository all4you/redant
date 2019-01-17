package com.redant.core.server;

import com.redant.core.common.constants.CommonConstants;
import com.redant.core.init.InitExecutor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * NettyHttpServer
 * @author houyi.wh
 * @date 2017-10-20
 */
public final class NettyHttpServer implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServer.class);

    @Override
    public void preStart() {
        InitExecutor.init();
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(CommonConstants.BOSS_GROUP_SIZE, new DefaultThreadFactory("boss", true));
        EventLoopGroup workerGroup = new NioEventLoopGroup(CommonConstants.WORKER_GROUP_SIZE, new DefaultThreadFactory("worker", true));
        try {
            long start = System.currentTimeMillis();
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
//             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new NettyHttpServerInitializer());

            ChannelFuture future = b.bind(CommonConstants.SERVER_PORT).sync();
            long cost = System.currentTimeMillis()-start;
            LOGGER.info("[NettyHttpServer] Startup at port:{} cost:{}[ms]",CommonConstants.SERVER_PORT,cost);

            // 等待服务端Socket关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("[NettyHttpServer] InterruptedException:",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}