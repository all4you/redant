package com.redant.core.handler;

import com.redant.core.TemporaryDataHolder;
import com.redant.core.common.constants.CommonConstants;
import com.redant.core.common.exception.InvocationException;
import com.redant.core.common.util.HttpRenderUtil;
import com.redant.core.controller.ControllerProxy;
import com.redant.core.controller.ProxyInvocation;
import com.redant.core.controller.context.ControllerContext;
import com.redant.core.controller.context.DefaultControllerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求分发控制器
 * @author houyi.wh
 * @date 2017-10-20
 */
public class ControllerDispatcher extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(ControllerDispatcher.class);

    private static ControllerContext controllerContext = DefaultControllerContext.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if(msg instanceof HttpRequest){
            HttpRequest request = (HttpRequest) msg;
            boolean forceClose = false;
            HttpResponse response;
            try{
                // 根据路由获得具体的ControllerProxy
                ControllerProxy controllerProxy = controllerContext.getProxy(request.method(),request.uri());
                if(controllerProxy == null){
                    forceClose = true;
                    response = HttpRenderUtil.getNotFoundResponse();
                }else {
                    // 每一个Controller的方法返回类型约定为Render的实现类
                    Object result = ProxyInvocation.invoke(controllerProxy);
                    response = HttpRenderUtil.render(result, controllerProxy.getRenderType());
                }
            }catch(Exception e){
                LOGGER.error("Server Internal Error,cause:",e);
                forceClose = true;
                if(e instanceof IllegalArgumentException || e instanceof InvocationException){
                    response = HttpRenderUtil.getErrorResponse(e.getMessage());
                }else{
                    response = HttpRenderUtil.getServerErrorResponse();
                }
            }
            TemporaryDataHolder.storeForceClose(forceClose);
            TemporaryDataHolder.storeHttpResponse(response);
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
        LOGGER.error("ctx close,cause:",cause);
    }


}
