package com.redant.core.interceptor;

import com.redant.core.TemporaryDataHolder;
import com.redant.core.common.util.HttpRenderUtil;
import com.redant.core.render.RenderType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 后置拦截器
 * @author houyi.wh
 * @date 2017/11/7
 */
public abstract class AfterHandleInterceptor extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(AfterHandleInterceptor.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 当拦截的方法返回false直接返回，否则进入下一个handler
        if(!afterHandle(ctx, msg)){
            // 释放ThreadLocal对象
            TemporaryDataHolder.removeAll();
            HttpResponse response = HttpRenderUtil.render(null,RenderType.TEXT);
            // 从该channel直接返回
            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        /*
         * 提交给下一个ChannelHandler去处理
         * 并且不需要调用ReferenceCountUtil.release(msg);来释放引用计数
         */
        ctx.fireChannelRead(msg);
    }

    /**
     * 后置拦截器拦截的方法
     */
    public abstract boolean afterHandle(ChannelHandlerContext ctx, Object msg);

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
