package com.redant.core.cookie;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 操作Cookie的辅助类
 * @author gris.wang
 * @date 2017/11/6
 */
public class CookieHelper {

    /**
     * 获取HttpRequest中的Cookies
     * @param request
     * @return
     */
    public static Set<Cookie> getCookies(HttpRequest request){
        Set<Cookie> cookies;
        String value = request.headers().get(HttpHeaderNames.COOKIE);
        if (value == null) {
            cookies = Collections.emptySet();
        } else {
            cookies = ServerCookieDecoder.STRICT.decode(value);
        }
        return cookies;
    }

    /**
     * 设置Cookie
     * @param response
     * @param cookie
     */
    public static void setCookie(HttpResponse response,Cookie cookie){
        response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
    }

    /**
     * 设置所有的Cookie
     * @param request
     * @param response
     */
    public static void setCookies(HttpRequest request,HttpResponse response){
        Set<Cookie> cookies = getCookies(request);
        if (!cookies.isEmpty()) {
            for (Cookie cookie : cookies) {
                setCookie(response,cookie);
            }
        }
    }


    /**
     * 添加一个Cookie
     * @param response response
     * @param name  cookie名字
     * @param value cookie值
     */
    public static void addCookie(HttpResponse response,String name,String value){
        CookieHelper.addCookie(response,name,value,null);
    }

    /**
     * 添加一个Cookie
     * @param response response
     * @param name  cookie名字
     * @param value cookie值
     * @param domain cookie所在域
     */
    public static void addCookie(HttpResponse response,String name,String value,String domain){
        CookieHelper.addCookie(response,name,value,domain,0);
    }


    /**
     * 添加一个Cookie
     * @param response response
     * @param name  cookie名字
     * @param value cookie值
     * @param maxAge cookie生命周期  以秒为单位
     */
    public static void addCookie(HttpResponse response,String name,String value,long maxAge){
        CookieHelper.addCookie(response,name,value,null,maxAge);
    }

    /**
     * 添加一个Cookie
     * @param response response
     * @param name  cookie名字
     * @param value cookie值
     * @param domain cookie所在域
     * @param maxAge cookie生命周期  以秒为单位
     */
    public static void addCookie(HttpResponse response,String name,String value,String domain,long maxAge){
        Cookie cookie = new DefaultCookie(name,value);
        cookie.setPath("/");
        if(domain!=null && domain.trim().length()>0) {
            cookie.setDomain(domain);
        }
        if(maxAge>0){
            cookie.setMaxAge(maxAge);
        }
        setCookie(response,cookie);
    }

    /**
     * 将cookie封装到Map里面
     * @param request HttpRequest
     * @return
     */
    public static Map<String,Cookie> getCookieMap(HttpRequest request){
        Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
        Set<Cookie> cookies = getCookies(request);
        if(null!=cookies && !cookies.isEmpty()){
            for(Cookie cookie : cookies){
                cookieMap.put(cookie.name(), cookie);
            }
        }
        return cookieMap;
    }

    /**
     * 根据名字获取Cookie
     * @param request HttpRequest
     * @param name cookie名字
     * @return
     */
    public static Cookie getCookie(HttpRequest request,String name){
        Map<String,Cookie> cookieMap = getCookieMap(request);
        return cookieMap.containsKey(name)?cookieMap.get(name):null;
    }

    /**
     * 获取Cookie的值
     * @param request HttpRequest
     * @param name cookie名字
     * @return
     */
    public static String getCookieValue(HttpRequest request,String name){
        Cookie cookie = getCookie(request,name);
        return cookie.value();
    }

    /**
     * 删除一个Cookie
     * @param request
     * @param response
     * @param name
     * @return
     */
    public static boolean deleteCookie(HttpRequest request,HttpResponse response,String name) {
        Cookie cookie = getCookie(request,name);
        if(cookie!=null){
            cookie.setMaxAge(0);
            cookie.setPath("/");
            setCookie(response,cookie);
            return true;
        }
        return false;
    }




}
