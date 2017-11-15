package com.redant.core.server;

import com.redant.common.constants.CommonConstants;
import com.redant.core.handler.ControllerDispatcher;
import com.redant.core.handler.DataStorer;
import com.redant.core.handler.ResponseWriter;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * NettyServer
 * @author gris.wang
 * @create 2017-10-20
 */
public final class NettyServer {

    private final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ServerInitializer());

            ChannelFuture future = b.bind(CommonConstants.SERVER_PORT).sync();
            logger.info("NettyServer Startup at port:{}",CommonConstants.SERVER_PORT);

            // 等待服务端Socket关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("InterruptedException:",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class ServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline p = ch.pipeline();

            // HttpServerCodec is a combination of HttpRequestDecoder and HttpResponseEncoder
            // 使用HttpServerCodec将ByteBuf编解码为httpRequest/httpResponse
            p.addLast(new HttpServerCodec());

            // add gizp compressor for http response content
            p.addLast(new HttpContentCompressor());

            // 指定最大的content_length
            p.addLast(new HttpObjectAggregator(CommonConstants.MAX_CONTENT_LENGTH));

            p.addLast(new ChunkedWriteHandler());

            p.addLast(new DataStorer());

            p.addLast(new ControllerDispatcher());

            p.addLast(new ResponseWriter());

        }
    }

}