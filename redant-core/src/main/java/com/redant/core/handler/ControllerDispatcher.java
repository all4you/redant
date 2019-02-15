package com.redant.core.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.redant.core.common.constants.CommonConstants;
import com.redant.core.common.exception.InvocationException;
import com.redant.core.common.util.HttpRenderUtil;
import com.redant.core.common.util.HttpRequestUtil;
import com.redant.core.context.RedantContext;
import com.redant.core.controller.ControllerProxy;
import com.redant.core.controller.ProxyInvocation;
import com.redant.core.controller.context.ControllerContext;
import com.redant.core.controller.context.DefaultControllerContext;
import com.redant.core.interceptor.InterceptorUtil;
import com.redant.core.render.RenderType;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 请求分发控制器
 * @author houyi.wh
 * @date 2017-10-20
 */
public class ControllerDispatcher extends SimpleChannelInboundHandler<HttpRequest> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ControllerDispatcher.class);

    private static ControllerContext controllerContext = DefaultControllerContext.getInstance();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest request) {
        // 暂存请求对象
        stageRequest(request,ctx);
        HttpResponse response = null;
        try{
            //获取参数列表
            Map<String, List<String>> paramMap = HttpRequestUtil.getParameterMap(RedantContext.currentContext().getRequest());
            // 处理前置拦截器
            if(!InterceptorUtil.preHandle(paramMap)){
                // 先从RedantContext中获取response，检查用户是否设置了response
                response = RedantContext.currentContext().getResponse();
                // 若用户没有设置就返回一个默认的
                if (response == null) {
                    response = HttpRenderUtil.getBlockedResponse();
                }
                return;
            }
            // 处理业务逻辑
            response = invokeResponse(request);
            // 处理后置拦截器
            InterceptorUtil.afterHandle(paramMap);
        }catch(Exception e){
            LOGGER.error("Server Internal Error,cause:",e);
            response = getErrorResponse(e);
        }finally {
            // 暂存响应对象
            stageResponse(response);
            // 写响应结果
            writeResponse();
            // 释放ThreadLocal对象
            RedantContext.clear();
        }
    }

    private void stageRequest(HttpRequest request, ChannelHandlerContext ctx){
        // 将request和context存储到ThreadLocal中去，便于后期在其他地方获取并使用
        RedantContext.currentContext().setRequest(request).setContext(ctx);
    }

    private HttpResponse invokeResponse(HttpRequest request) throws Exception {
        HttpResponse response;
        // 根据路由获得具体的ControllerProxy
        ControllerProxy controllerProxy = controllerContext.getProxy(request.method(),request.uri());
        if(controllerProxy == null){
            response = HttpRenderUtil.getNotFoundResponse();
        }else {
            // 调用用户自定义的Controller，获得结果
            Object result = ProxyInvocation.invoke(controllerProxy);
            response = HttpRenderUtil.render(result, controllerProxy.getRenderType());
        }
        return response;
    }

    private HttpResponse getErrorResponse(Exception e){
        HttpResponse response;
        if(e instanceof IllegalArgumentException || e instanceof InvocationException){
            response = HttpRenderUtil.getErrorResponse(e.getMessage());
        }else{
            response = HttpRenderUtil.getServerErrorResponse();
        }
        return response;
    }

    private void stageResponse(HttpResponse response){
        RedantContext.currentContext().setResponse(response);
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
        // 取出当前RedantContext
        RedantContext redantContext = RedantContext.currentContext();
        // 取出各种对象
        ChannelHandlerContext ctx = redantContext.getContext();
        HttpRequest request = redantContext.getRequest();
        FullHttpResponse response = (FullHttpResponse) redantContext.getResponse();
        // 构造响应头
        buildHeaders(response, redantContext);
        // 写响应数据
        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if(isClose(request)){
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void buildHeaders(FullHttpResponse response, RedantContext redantContext){
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(response.content().readableBytes()));
        // 写cookie
        Set<Cookie> cookies = redantContext.getCookies();
        if(CollectionUtil.isNotEmpty(cookies)){
            for(Cookie cookie : cookies){
                response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
            }
        }
    }

    private boolean isClose(HttpRequest request){
        return request.headers().contains(HttpHeaderNames.CONNECTION, CommonConstants.CONNECTION_CLOSE, true) ||
                (request.protocolVersion().equals(HttpVersion.HTTP_1_0) && !request.headers().contains(HttpHeaderNames.CONNECTION, CommonConstants.CONNECTION_KEEP_ALIVE, true));
    }

}
