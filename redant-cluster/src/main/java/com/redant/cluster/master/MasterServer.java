package com.redant.cluster.master;

import com.redant.cluster.service.discover.DiscoveryWrapper;
import com.redant.core.common.constants.CommonConstants;
import com.redant.core.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * MasterServer
 * @author gris.wang
 * @date 2017/11/20
 */
public final class MasterServer implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterServer.class);

    private String zkServerAddress;

    public MasterServer(String zkServerAddress){
        this.zkServerAddress = zkServerAddress;
    }

    @Override
    public void preStart() {
        // 监听SlaveNode的变化
        DiscoveryWrapper.watchSlave(zkServerAddress);
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(CommonConstants.BOSS_GROUP_SIZE, new DefaultThreadFactory("boss", true));
        EventLoopGroup workerGroup = new NioEventLoopGroup(CommonConstants.WORKER_GROUP_SIZE, new DefaultThreadFactory("worker", true));
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new MasterServerInitializer());

            ChannelFuture future = b.bind(CommonConstants.SERVER_PORT).sync();
            LOGGER.info("MasterServer Startup at port:{}",CommonConstants.SERVER_PORT);

            // 等待服务端Socket关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException:",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class MasterServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();

            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpContentCompressor());
            pipeline.addLast(new HttpObjectAggregator(CommonConstants.MAX_CONTENT_LENGTH));
            pipeline.addLast(new ChunkedWriteHandler());
            pipeline.addLast(new MasterServerHandler());
        }
    }

}