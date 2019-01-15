package com.redant.core.controller.context;

import com.redant.core.controller.ControllerProxy;
import io.netty.handler.codec.http.HttpMethod;

/**
 * @author houyi.wh
 * @date 2019-01-15
 */
public interface ControllerContext {

    /**
     * 添加Controller代理
     * @param path 请求路径
     * @param proxy 代理
     */
    void addProxy(String path,ControllerProxy proxy);

    /**
     * 获取Controller代理
     * @param method 请求方法类型
     * @param uri 请求url
     * @return 代理
     */
    ControllerProxy getProxy(HttpMethod method, String uri);
}