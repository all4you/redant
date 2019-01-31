package com.redant.core.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.redant.core.TemporaryDataHolder;
import com.redant.core.common.constants.CommonConstants;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 写响应
 * @author houyi.wh
 * @date 2017/11/7
 */
public class ResponseWriter extends SimpleChannelInboundHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ResponseWriter.class);

    private HttpRequest request;
    private Channel channel;
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
            response = TemporaryDataHolder.loadHttpResponse();
            writeResponse();
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        // 释放ThreadLocal对象
        TemporaryDataHolder.removeAll();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        LOGGER.error("ctx close,cause:",cause);
    }

    /**
     * 响应消息
     */
    private void writeResponse(){
        boolean close = isClose();
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(response.content().readableBytes()));
        // 写cookie
        Set<Cookie> cookies = TemporaryDataHolder.loadCookies();
        if(!CollectionUtil.isEmpty(cookies)){
            for(Cookie cookie : cookies){
                response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
            }
        }
        ChannelFuture future = channel.write(response);
        if(close){
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private boolean isClose(){
        return request.headers().contains(HttpHeaderNames.CONNECTION, CommonConstants.CONNECTION_CLOSE, true) ||
                (request.protocolVersion().equals(HttpVersion.HTTP_1_0) && !request.headers().contains(HttpHeaderNames.CONNECTION, CommonConstants.CONNECTION_KEEP_ALIVE, true));
    }

}
