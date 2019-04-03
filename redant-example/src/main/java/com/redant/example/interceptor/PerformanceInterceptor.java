package com.redant.example.interceptor;

import com.redant.core.anno.Order;
import com.redant.core.context.RedantContext;
import com.redant.core.interceptor.Interceptor;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 该拦截器可以计算出用户自定义 Controller 方法的执行时间
 * @author houyi
 **/
@Order(value = 2)
public class PerformanceInterceptor extends Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceInterceptor.class);

    private volatile long start;

    @Override
    public boolean preHandle(Map<String, List<String>> paramMap) {
        start = System.currentTimeMillis();
        return true;
    }

    @Override
    public void postHandle(Map<String, List<String>> paramMap) {
        long end = System.currentTimeMillis();
        long cost = end - start;
        HttpRequest request = RedantContext.currentContext().getRequest();
        String uri = request.uri();
        LOGGER.info("uri={}, cost:{}[ms]", uri, cost);
    }

}
