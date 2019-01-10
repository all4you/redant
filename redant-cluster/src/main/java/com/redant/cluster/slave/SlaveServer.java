package com.redant.cluster.slave;

import com.redant.cluster.service.register.RegisteryWrapper;
import com.redant.core.bean.BeanContext;
import com.redant.core.common.constants.CommonConstants;
import com.redant.core.router.RouterContext;
import com.redant.core.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SlaveServer
 * @author gris.wang
 * @date 2017/11/20
 */
public final class SlaveServer implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlaveServer.class);

    private String zkServerAddress;
    private Node node;

    public SlaveServer(String zkServerAddress, Node node){
        this.zkServerAddress = zkServerAddress;
        this.node = node;
    }

    @Override
    public void preStart() {
        BeanContext.initBeans();
        RouterContext.initRouters();
        // 注册Slave到ZK
        RegisteryWrapper.register(zkServerAddress, node);
    }

    @Override
    public void start() {
        if(node ==null){
            throw new IllegalArgumentException("slave is null");
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup(CommonConstants.BOSS_GROUP_SIZE, new DefaultThreadFactory("boss", true));
        EventLoopGroup workerGroup = new NioEventLoopGroup(CommonConstants.WORKER_GROUP_SIZE, new DefaultThreadFactory("worker", true));
        try {
            long start = System.currentTimeMillis();
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
//             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new SlaveServerInitializer());

            ChannelFuture future = b.bind(node.getPort()).sync();
            long cost = System.currentTimeMillis()-start;
            LOGGER.info("SlaveServer Startup at port:{} cost:{}[ms]", node.getPort(),cost);

            // 等待服务端Socket关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException:",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class SlaveServerInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();

            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpContentCompressor());
            pipeline.addLast(new HttpObjectAggregator(CommonConstants.MAX_CONTENT_LENGTH));
            pipeline.addLast(new ChunkedWriteHandler());
            pipeline.addLast(new SlaveServerHandler());
        }
    }

}