package com.redant.core.interceptor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 前置拦截器
 * @author gris.wang
 * @since 2017/11/7
 */
public abstract class PreHandleInterceptor extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(PreHandleInterceptor.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 当拦截的方法返回false直接返回，否则进入下一个handler
        if(!preHandle(ctx, msg)){
            return;
        }

        /**
         * 提交给下一个ChannelHandler去处理
         * 并且不需要调用ReferenceCountUtil.release(msg);来释放引用计数
         */
        ctx.fireChannelRead(msg);
    }

    /**
     * 前置拦截器拦截的方法
     * @param ctx
     * @param msg
     * @return
     */
    public abstract boolean preHandle(ChannelHandlerContext ctx, Object msg);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        logger.error("ctx close,cause:",cause);
    }


}
