package com.redant.core.handler;

import com.redant.common.constants.CommonConstants;
import com.redant.core.DataHolder;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 写响应
 * @author gris.wang
 * @since 2017/11/7
 */
public class ResponseWriter extends SimpleChannelInboundHandler {

    private final static Logger logger = LoggerFactory.getLogger(ResponseWriter.class);

    private HttpRequest request;
    private Channel channel;
    private boolean forceClose;
    private FullHttpResponse response;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest){
            request = (HttpRequest)msg;
            if(request.uri().equals(CommonConstants.FAVICON_ICO)){
                return;
            }
            if (HttpUtil.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            channel = ctx.channel();
            forceClose = DataHolder.getForceClose();
            response = DataHolder.getHttpResponse();
            writeResponse();
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        // 释放ThreadLocal对象
        DataHolder.removeAll();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        logger.error("ctx close,cause:",cause);
    }

    /**
     * 响应消息
     */
    private void writeResponse(){
        boolean close = isClose();
        if(!close && !forceClose){
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(response.content().readableBytes()));
        }
        ChannelFuture future = channel.write(response);
        if(close || forceClose){
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private boolean isClose(){
        return request.headers().contains(HttpHeaderNames.CONNECTION, CommonConstants.CONNECTION_CLOSE, true) ||
                (request.protocolVersion().equals(HttpVersion.HTTP_1_0) && !request.headers().contains(HttpHeaderNames.CONNECTION, CommonConstants.CONNECTION_KEEP_ALIVE, true));
    }

}
