package com.redant.core.cookie;

import com.redant.core.DataHolder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.Map;
import java.util.Set;

/**
 * Cookie管理器
 * @author gris.wang
 * @date 2017/11/6
 */

public class CookieManager {

    /**
     * 获取HttpRequest中的Cookies
     * @return
     */
    public static Set<Cookie> getCookies(){
        HttpRequest request = DataHolder.getHttpRequest();
        return CookieHelper.getCookies(request);
    }

    /**
     * 设置Cookie
     * @param cookie
     */
    @Deprecated
    public static void setCookie(Cookie cookie){
        HttpResponse response = DataHolder.getHttpResponse();
        CookieHelper.setCookie(response,cookie);
    }

    /**
     * 设置所有的Cookie
     */
    @Deprecated
    public static void setCookies(){
        HttpRequest request = DataHolder.getHttpRequest();
        HttpResponse response = DataHolder.getHttpResponse();
        CookieHelper.setCookies(request,response);
    }

    /**
     * 添加一个Cookie
     * @param name  cookie名字
     * @param value cookie值
     */
    @Deprecated
    public static void addCookie(String name,String value){
        HttpResponse response = DataHolder.getHttpResponse();
        CookieHelper.addCookie(response,name,value,null);
    }

    /**
     * 添加一个Cookie
     * @param name  cookie名字
     * @param value cookie值
     * @param domain cookie所在域
     */
    @Deprecated
    public static void addCookie(String name,String value,String domain){
        HttpResponse response = DataHolder.getHttpResponse();
        CookieHelper.addCookie(response,name,value,domain,0);
    }


    /**
     * 添加一个Cookie
     * @param name  cookie名字
     * @param value cookie值
     * @param maxAge cookie生命周期  以秒为单位
     */
    @Deprecated
    public static void addCookie(String name,String value,long maxAge){
        HttpResponse response = DataHolder.getHttpResponse();
        CookieHelper.addCookie(response,name,value,null,maxAge);
    }


    /**
     * 添加一个Cookie
     * @param name  cookie名字
     * @param value cookie值
     * @param domain cookie所在域
     * @param maxAge cookie生命周期  以秒为单位
     */
    @Deprecated
    public static void addCookie(String name,String value,String domain,long maxAge){
        HttpResponse response = DataHolder.getHttpResponse();
        CookieHelper.addCookie(response,name,value,domain,maxAge);
    }

    /**
     * 将cookie封装到Map里面
     * @return
     */
    public static Map<String,Cookie> getCookieMap(){
        HttpRequest request = DataHolder.getHttpRequest();
        return CookieHelper.getCookieMap(request);
    }

    /**
     * 根据名字获取Cookie
     * @param name cookie名字
     * @return
     */
    public static Cookie getCookie(String name){
        HttpRequest request = DataHolder.getHttpRequest();
        return CookieHelper.getCookie(request,name);
    }

    /**
     * 获取Cookie的值
     * @param name cookie名字
     * @return
     */
    public static String getCookieValue(String name){
        HttpRequest request = DataHolder.getHttpRequest();
        return CookieHelper.getCookieValue(request,name);
    }

    /**
     * 删除一个Cookie
     * @param name
     * @return
     */
    @Deprecated
    public static boolean deleteCookie(String name) {
        HttpRequest request = DataHolder.getHttpRequest();
        HttpResponse response = DataHolder.getHttpResponse();
        return CookieHelper.deleteCookie(request,response,name);
    }

}
