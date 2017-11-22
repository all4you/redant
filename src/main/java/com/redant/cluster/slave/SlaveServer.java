package com.redant.cluster.slave;

import com.redant.common.constants.CommonConstants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SlaveServer
 * @author gris.wang
 * @since 2017/11/20
 */
public final class SlaveServer {

    private final Logger logger = LoggerFactory.getLogger(SlaveServer.class);

    public void start(SlaveNode slaveNode) {
        if(slaveNode==null){
            throw new IllegalArgumentException("slaveNode is null");
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup(CommonConstants.BOSS_GROUP_SIZE, new DefaultThreadFactory("boss", true));
        EventLoopGroup workerGroup = new NioEventLoopGroup(CommonConstants.WORKER_GROUP_SIZE, new DefaultThreadFactory("worker", true));
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new SlaveServerInitializer());

            ChannelFuture future = b.bind(slaveNode.getPort()).sync();
            logger.info("SlaveServer Startup at port:{}",slaveNode.getPort());

            // 等待服务端Socket关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("InterruptedException:",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class SlaveServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();

            // 服务端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
            pipeline.addLast(new HttpResponseEncoder());
            pipeline.addLast(new HttpContentCompressor());
            // 指定最大的content_length
            pipeline.addLast(new HttpObjectAggregator(CommonConstants.MAX_CONTENT_LENGTH));
            pipeline.addLast(new ChunkedWriteHandler());
            // 服务端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
            pipeline.addLast(new HttpRequestDecoder());

            pipeline.addLast(new SlaveServerHandler());
        }
    }

}