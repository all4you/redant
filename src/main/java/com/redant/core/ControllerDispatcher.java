package com.redant.core;

import com.redant.common.exception.InvocationException;
import com.redant.core.cookie.CookieHelper;
import com.redant.core.invocation.ProxyInvocation;
import com.redant.core.invocation.ControllerProxy;
import com.redant.core.render.Render;
import com.redant.core.render.RenderType;
import com.redant.core.router.RouteResult;
import com.redant.core.router.RouterContext;
import com.redant.common.util.HttpRenderUtil;
import com.redant.common.constants.HttpHeaders;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 控制器
 * @author gris.wang
 * @create 2017-10-20
 */
public class ControllerDispatcher extends SimpleChannelInboundHandler {

    private final static Logger logger = LoggerFactory.getLogger(ControllerDispatcher.class);

    private final static Logger routerLogger = LoggerFactory.getLogger("routerMsgLog");

    private static final String CONNECTION_KEEP_ALIVE = "keep-alive";
    private static final String CONNECTION_CLOSE = "close";

    private final static String FAVICON_ICO = "/favicon.ico";

    private HttpRequest request;
    private FullHttpResponse response;
    private Channel channel;
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        DataHolder.removeRequest(); // 释放ThreadLocal对象
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof HttpRequest){
            channel = ctx.channel();
            request = (HttpRequest) msg;

            if (HttpUtil.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            if(request.uri().equals(FAVICON_ICO)){
                return;
            }
            try{
                DataHolder.storeRequest(request);

                // 获得路由结果
                RouteResult<RenderType> routeResult = RouterContext.getRouteResult(request.method(),request.uri());
                // 根据路由获得具体的ControllerProxy
                ControllerProxy controllerProxy = RouterContext.getControllerProxy(routeResult);
                if(controllerProxy == null){
                    response = HttpRenderUtil.getNotFoundResponse();
                    writeResponse(true);
                    return;
                }

                // 每一个Controller的方法返回类型约定为Render的实现类
                Render render = ProxyInvocation.invoke(controllerProxy);

                if(render != null){
                    response = render.process();
                }
                writeResponse(false);

            }catch(Exception e){
                routerLogger.error("Server Internal Error,cause:",e);
                if(e instanceof IllegalArgumentException || e instanceof InvocationException){
                    response = HttpRenderUtil.getErrorResponse(e.getMessage());
                }else{
                    response = HttpRenderUtil.getServerErrorResponse();
                }
                writeResponse(true);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        logger.error("ctx close,cause:",cause);
    }

    private void writeResponse(boolean forceClose){
        boolean close = isClose();
        if(!close && !forceClose){
            response.headers().add(HttpHeaders.CONTENT_LENGTH, String.valueOf(response.content().readableBytes()));
        }
        CookieHelper.setCookie(request,response);
        ChannelFuture future = channel.write(response);
        if(close || forceClose){
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }




    private boolean isClose(){
        return request.headers().contains(HttpHeaders.CONNECTION, CONNECTION_CLOSE, true) ||
                (request.protocolVersion().equals(HttpVersion.HTTP_1_0) && !request.headers().contains(HttpHeaders.CONNECTION, CONNECTION_KEEP_ALIVE, true));
    }

}
