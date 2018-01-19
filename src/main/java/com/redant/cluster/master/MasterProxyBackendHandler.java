package com.redant.cluster.master;

import io.netty.channel.*;

/**
 * @author gris.wang
 * @since 2018/1/18
 **/
public class MasterProxyBackendHandler extends ChannelInboundHandlerAdapter {

    private final Channel inboundChannel;

    public MasterProxyBackendHandler(Channel inboundChannel){
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    future.channel().close();
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        MasterProxyHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        MasterProxyHandler.closeOnFlush(ctx.channel());
    }


}
