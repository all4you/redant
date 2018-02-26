package com.redant.cluster.node;

import com.redant.common.exception.InvocationException;
import com.redant.common.util.HttpRenderUtil;
import com.redant.core.DataHolder;
import com.redant.core.invocation.ControllerProxy;
import com.redant.core.invocation.ProxyInvocation;
import com.redant.core.render.Render;
import com.redant.core.render.RenderType;
import com.redant.core.router.RouteResult;
import com.redant.core.router.RouterContext;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NodeServerHandler
 * @author gris.wang
 * @since 2017/11/22
 */
public class NodeServerHandler extends SimpleChannelInboundHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(NodeServerHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        FullHttpResponse response = HttpRenderUtil.render(null,HttpRenderUtil.CONTENT_TYPE_TEXT);
        if(msg instanceof HttpRequest){
            HttpRequest request = (HttpRequest) msg;
            DataHolder.store(DataHolder.HolderType.REQUEST,request);
            try{
                // 获得路由结果
                RouteResult<RenderType> routeResult = RouterContext.getRouteResult(request.method(),request.uri());
                // 根据路由获得具体的ControllerProxy
                ControllerProxy controllerProxy = RouterContext.getControllerProxy(routeResult);
                if(controllerProxy == null){
                    response = HttpRenderUtil.getNotFoundResponse();
                }else {
                    // 每一个Controller的方法返回类型约定为Render的实现类
                    Render render = ProxyInvocation.invoke(controllerProxy);
                    response = render.response();
                }
            }catch(Exception e){
                LOGGER.error("Slave Server channelRead0 error,cause:",e);
                if(e instanceof IllegalArgumentException || e instanceof InvocationException){
                    response = HttpRenderUtil.getErrorResponse(e.getMessage());
                }else{
                    response = HttpRenderUtil.getServerErrorResponse();
                }
            }
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        DataHolder.removeAll();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("NodeServerHandler exceptionCaught,cause:",cause);
        ctx.close();
    }


}
