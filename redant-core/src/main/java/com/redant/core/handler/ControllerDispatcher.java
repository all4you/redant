package com.redant.core.handler;

import com.redant.core.common.constants.CommonConstants;
import com.redant.core.executor.Executor;
import com.redant.core.executor.HttpResponseExecutor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 请求分发控制器
 *
 * @author houyi.wh
 * @date 2017-10-20
 */
public class ControllerDispatcher extends SimpleChannelInboundHandler<HttpRequest> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ControllerDispatcher.class);

    private static Executor<HttpResponse> executor = HttpResponseExecutor.getInstance();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest request) {
        if (CommonConstants.ASYNC_EXECUTE_EVENT) {
            // 当前通道所持有的线程池
            EventExecutor channelExecutor = ctx.executor();
            // 创建一个异步结果，并指定该promise
            Promise<HttpResponse> promise = new DefaultPromise<>(channelExecutor);
            // 在自定义线程池中执行业务逻辑，并返回一个异步结果
            Future<HttpResponse> future = executor.asyncExecute(promise, request);
            future.addListener(new GenericFutureListener<Future<HttpResponse>>() {
                @Override
                public void operationComplete(Future<HttpResponse> f) throws Exception {
                    // 异步结果执行成功后，取出结果
                    HttpResponse response = f.get();
                    // 通过IO线程写响应结果
                    ctx.channel().writeAndFlush(response);
                }
            });
        } else {
            // 同步执行
            HttpResponse response = executor.execute(request);
            ctx.channel().writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        LOGGER.error("ctx close,cause:", cause);
    }

}
