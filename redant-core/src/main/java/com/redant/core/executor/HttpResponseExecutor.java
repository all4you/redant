package com.redant.core.executor;

import cn.hutool.core.collection.CollectionUtil;
import com.redant.core.common.exception.InvocationException;
import com.redant.core.common.util.HttpRenderUtil;
import com.redant.core.common.util.HttpRequestUtil;
import com.redant.core.context.RedantContext;
import com.redant.core.controller.ControllerProxy;
import com.redant.core.controller.ProxyInvocation;
import com.redant.core.controller.context.ControllerContext;
import com.redant.core.controller.context.DefaultControllerContext;
import com.redant.core.interceptor.InterceptorHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author houyi
 */
public class HttpResponseExecutor extends AbstractExecutor<HttpResponse> {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpResponseExecutor.class);

    private static ControllerContext controllerContext = DefaultControllerContext.getInstance();

    public static HttpResponseExecutor getInstance() {
        return HttpResponseExecutorHolder.executor;
    }

    private HttpResponseExecutor() {
    }

    @Override
    public HttpResponse doExecute(Object... request) {
        HttpRequest httpRequest = (HttpRequest) request[0];
        // 暂存请求对象
        // 将request存储到ThreadLocal中去，便于后期在其他地方获取并使用
        RedantContext.currentContext().setRequest(httpRequest);
        HttpResponse response = null;
        try {
            // 获取参数列表
            Map<String, List<String>> paramMap = HttpRequestUtil.getParameterMap(httpRequest);
            // 处理拦截器的前置方法
            if (!InterceptorHandler.preHandle(paramMap)) {
                // 先从RedantContext中获取response，检查用户是否设置了response
                response = RedantContext.currentContext().getResponse();
                // 若用户没有设置就返回一个默认的
                if (response == null) {
                    response = HttpRenderUtil.getBlockedResponse();
                }
            } else {
                // 处理业务逻辑
                response = invoke(httpRequest);
                // 处理拦截器的后置方法
                InterceptorHandler.postHandle(paramMap);
            }
        } catch (Exception e) {
            LOGGER.error("Server Internal Error,cause:", e);
            response = getErrorResponse(e);
        } finally {
            // 构造响应头
            buildHeaders(response, RedantContext.currentContext());
            // 释放ThreadLocal对象
            RedantContext.clear();
        }
        return response;
    }

    private HttpResponse invoke(HttpRequest request) throws Exception {
        // 根据路由获得具体的ControllerProxy
        ControllerProxy controllerProxy = controllerContext.getProxy(request.method(), request.uri());
        if (controllerProxy == null) {
            return HttpRenderUtil.getNotFoundResponse();
        }
        // 调用用户自定义的Controller，获得结果
        Object result = ProxyInvocation.invoke(controllerProxy);
        return HttpRenderUtil.render(result, controllerProxy.getRenderType());
    }

    private HttpResponse getErrorResponse(Exception e) {
        HttpResponse response;
        if (e instanceof IllegalArgumentException || e instanceof InvocationException) {
            response = HttpRenderUtil.getErrorResponse(e.getMessage());
        } else {
            response = HttpRenderUtil.getServerErrorResponse();
        }
        return response;
    }

    private void buildHeaders(HttpResponse response, RedantContext redantContext) {
        if (response == null) {
            return;
        }
        FullHttpResponse fullHttpResponse = (FullHttpResponse) response;
        fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(fullHttpResponse.content().readableBytes()));
        // 写cookie
        Set<Cookie> cookies = redantContext.getCookies();
        if (CollectionUtil.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                fullHttpResponse.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
            }
        }
    }

    private static final class HttpResponseExecutorHolder {
        private static HttpResponseExecutor executor = new HttpResponseExecutor();
    }
}
