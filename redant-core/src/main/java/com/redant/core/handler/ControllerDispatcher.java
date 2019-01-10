package com.redant.core.handler;

import com.redant.core.common.exception.InvocationException;
import com.redant.core.common.util.HttpRenderUtil;
import com.redant.core.DataHolder;
import com.redant.core.invocation.ControllerProxy;
import com.redant.core.invocation.ProxyInvocation;
import com.redant.core.render.Render;
import com.redant.core.render.RenderType;
import com.redant.core.router.RouteResult;
import com.redant.core.router.RouterContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求分发控制器
 * @author gris.wang
 * @date 2017-10-20
 */
public class ControllerDispatcher extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(ControllerDispatcher.class);

    private final static Logger routerLogger = LoggerFactory.getLogger("routerMsgLog");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof HttpRequest){
            HttpRequest request = (HttpRequest) msg;
            boolean forceClose = false;
            HttpResponse response;
            try{
                // 获得路由结果
                RouteResult<RenderType> routeResult = RouterContext.getRouteResult(request.method(),request.uri());
                if(routeResult==null){
                    forceClose = true;
                    response = HttpRenderUtil.getNotFoundResponse();
                }else{
                    // 根据路由获得具体的ControllerProxy
                    ControllerProxy controllerProxy = RouterContext.getControllerProxy(routeResult);
                    if(controllerProxy == null){
                        forceClose = true;
                        response = HttpRenderUtil.getNotFoundResponse();
                    }else {
                        // 每一个Controller的方法返回类型约定为Render的实现类
                        Render render = ProxyInvocation.invoke(controllerProxy);
                        response = render.response();
                    }
                }
            }catch(Exception e){
                routerLogger.error("Server Internal Error,cause:",e);
                forceClose = true;
                if(e instanceof IllegalArgumentException || e instanceof InvocationException){
                    response = HttpRenderUtil.getErrorResponse(e.getMessage());
                }else{
                    response = HttpRenderUtil.getServerErrorResponse();
                }
            }
            DataHolder.store(DataHolder.HolderType.FORCE_CLOSE,forceClose);
            DataHolder.store(DataHolder.HolderType.RESPONSE,response);
        }
        /*
         * 提交给下一个ChannelHandler去处理
         * 并且不需要调用ReferenceCountUtil.release(msg);来释放引用计数
         */
        ctx.fireChannelRead(msg);
    }

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
