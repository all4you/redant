package com.redant.core.cookie;

import io.netty.handler.codec.http.cookie.Cookie;

import java.util.Map;
import java.util.Set;

/**
 * cookie管理
 * @author houyi.wh
 * @date 2019-01-16
 */
public interface CookieManager {

    /**
     * 获取所有的cookie
     * @return cookie集合
     */
    Set<Cookie> getCookies();

    /**
     * 获取所有的cookie，并返回一个Map
     * @return cookie的map
     */
    Map<String,Cookie> getCookieMap();

    /**
     * 根据名称获取cookie
     * @param name 名称
     * @return cookie
     */
    Cookie getCookie(String name);

    /**
     * 根据名称获取cookie的值
     * @param name 名称
     * @return cookie的值
     */
    String getCookieValue(String name);

    /**
     * 设置cookie到响应结果中
     * @param cookie cookie
     */
    void setCookie(Cookie cookie);

    /**
     * 获取所有的cookie后，全部设置到响应结果中
     */
    void setCookies();

    /**
     * 添加一个cookie
     * @param name cookie的名称
     * @param value cookie的值
     */
    void addCookie(String name,String value);

    /**
     * 添加一个cookie
     * @param name cookie的名称
     * @param value cookie的值
     * @param domain cookie的作用域
     */
    void addCookie(String name,String value,String domain);

    /**
     * 添加一个cookie
     * @param name cookie的名称
     * @param value cookie的值
     * @param maxAge cookie的有效期
     */
    void addCookie(String name,String value,long maxAge);

    /**
     * 添加一个cookie
     * @param name cookie的名称
     * @param value cookie的值
     * @param domain cookie的作用域
     * @param maxAge cookie的有效期
     */
    void addCookie(String name,String value,String domain,long maxAge);

    /**
     * 删除一个cookie
     * @param name cookie的名称
     * @return 操作结果
     */
    boolean deleteCookie(String name);

}