package com.redant.cluster.master;

import com.redant.cluster.slave.Node;
import com.redant.cluster.service.discover.DiscoveryWrapper;
import com.redant.core.common.constants.CommonConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MasterServerHandler is a http master which
 * will transfer http request to slave server
 * @author gris.wang
 * @date 2017/11/20
 */
public class MasterServerHandler extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(MasterServerHandler.class);

    private Node slave;

    /**
     * Client--->Master Channel
     */
    private Channel inboundChannel;

    /**
     * Master--->Slave Channel
     */
    private Channel outboundChannel;

    private ChannelFuture outboundConnectFuture;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        slave = DiscoveryWrapper.nextSlave();
        inboundChannel = ctx.channel();

        // Start the connection attempt.
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                // use master inboundChannel to write back the response get from remote server
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(CommonConstants.MAX_CONTENT_LENGTH));
                        pipeline.addLast(new MasterServerBackendHandler(inboundChannel));
                    }
                });
        bootstrap.option(ChannelOption.AUTO_READ, false);
        // connect to slave
        outboundConnectFuture = bootstrap.connect(slave.getHost(), slave.getPort());
        // get outboundChannel to remote server
        outboundChannel = outboundConnectFuture.channel();

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        outboundConnectFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    inboundChannel.read();
                    if(outboundChannel.isActive()) {
                        if(msg instanceof HttpRequest){
                            HttpRequest request = (HttpRequest)msg;
                            if(request.uri().equals(CommonConstants.FAVICON_ICO)){
                                return;
                            }
                            outboundChannel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) {
                                    if (future.isSuccess()) {
                                        // was able to flush out data, start to read the next chunk
                                        ctx.channel().read();
                                    } else {
                                        LOGGER.error("write to backend {}:{} error,cause:{}", slave.getHost(), slave.getPort(),future.cause());
                                        future.channel().close();
                                    }
                                }
                            });
                        }else{
                            closeOnFlush(ctx.channel());
                        }
                    }
                } else {
                    LOGGER.error("connect to backend {}:{} error,cause:{}", slave.getHost(), slave.getPort(),future.cause());
                    // Close the connection if the connection attempt has failed.
                    inboundChannel.close();
                }
            }
        });

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    static void closeOnFlush(Channel channel) {
        if (channel.isActive()) {
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

}
