package com.redant.cluster.slave;

import com.redant.cluster.node.Node;
import com.redant.cluster.service.register.ZkServiceRegister;
import com.redant.core.common.constants.CommonConstants;
import com.redant.core.init.InitExecutor;
import com.redant.core.server.NettyHttpServerInitializer;
import com.redant.core.server.Server;
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
 * SlaveServer
 * @author houyi.wh
 * @date 2017/11/20
 */
public final class SlaveServer implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlaveServer.class);

    private String zkAddress;
    private Node node;

    public SlaveServer(String zkAddress, Node node){
        this.zkAddress = zkAddress;
        this.node = node;
    }

    @Override
    public void preStart() {
        InitExecutor.init();
        // 注册Slave到ZK
        ZkServiceRegister.getInstance(zkAddress).register(node);
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
             .childHandler(new NettyHttpServerInitializer());

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

}