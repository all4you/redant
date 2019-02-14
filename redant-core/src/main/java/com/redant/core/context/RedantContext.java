package com.redant.core.context;

import cn.hutool.core.collection.CollectionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.util.concurrent.FastThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author houyi
 **/
public class RedantContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedantContext.class);

    /**
     * 使用FastThreadLocal替代JDK自带的ThreadLocal以提升并发性能
     */
    private static final FastThreadLocal<RedantContext> CONTEXT_HOLDER = new FastThreadLocal<>();

    private HttpRequest request;

    private ChannelHandlerContext context;

    private HttpResponse response;

    private Set<Cookie> cookies;

    private RedantContext(){

    }

    public RedantContext setRequest(HttpRequest request){
        this.request = request;
        return this;
    }

    public RedantContext setContext(ChannelHandlerContext context){
        this.context = context;
        return this;
    }

    public RedantContext setResponse(HttpResponse response){
        this.response = response;
        return this;
    }

    public RedantContext addCookie(Cookie cookie){
        if(cookie!=null){
            if(CollectionUtil.isEmpty(cookies)){
                cookies = new HashSet<>();
            }
            cookies.add(cookie);
        }
        return this;
    }

    public RedantContext addCookies(Set<Cookie> cookieSet){
        if(CollectionUtil.isNotEmpty(cookieSet)){
            if(CollectionUtil.isEmpty(cookies)){
                cookies = new HashSet<>();
            }
            cookies.addAll(cookieSet);
        }
        return this;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public Set<Cookie> getCookies() {
        return cookies;
    }

    public static RedantContext currentContext(){
        RedantContext context = CONTEXT_HOLDER.get();
        if(context==null){
            context = new RedantContext();
            CONTEXT_HOLDER.set(context);
        }
        return context;
    }

    public static void clear(){
        LOGGER.info("RedantContext removed");
        CONTEXT_HOLDER.remove();
    }


}
