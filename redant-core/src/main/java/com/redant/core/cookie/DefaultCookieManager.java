package com.redant.core.cookie;

import cn.hutool.core.util.StrUtil;
import com.redant.core.context.RedantContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Cookie管理器
 *
 * @author houyi.wh
 * @date 2019-01-16
 */

public class DefaultCookieManager implements CookieManager {

    private static final class DefaultCookieManagerHolder {
        private static DefaultCookieManager cookieManager = new DefaultCookieManager();
    }

    private DefaultCookieManager() {

    }

    public static CookieManager getInstance() {
        return DefaultCookieManagerHolder.cookieManager;
    }


    @Override
    public Set<Cookie> getCookies() {
        HttpRequest request = RedantContext.currentContext().getRequest();
        Set<Cookie> cookies = new HashSet<>();
        if (request != null) {
            String value = request.headers().get(HttpHeaderNames.COOKIE);
            if (value != null) {
                cookies = ServerCookieDecoder.STRICT.decode(value);
            }
        }
        return cookies;
    }

    @Override
    public Map<String, Cookie> getCookieMap() {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Set<Cookie> cookies = getCookies();
        if (null != cookies && !cookies.isEmpty()) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.name(), cookie);
            }
        }
        return cookieMap;
    }

    @Override
    public Cookie getCookie(String name) {
        Map<String, Cookie> cookieMap = getCookieMap();
        return cookieMap.getOrDefault(name, null);
    }

    @Override
    public String getCookieValue(String name) {
        Cookie cookie = getCookie(name);
        return cookie == null ? null : cookie.value();
    }

    @Override
    public void setCookie(Cookie cookie) {
        RedantContext.currentContext().addCookie(cookie);
    }

    @Override
    public void setCookies() {
        Set<Cookie> cookies = getCookies();
        if (!cookies.isEmpty()) {
            for (Cookie cookie : cookies) {
                setCookie(cookie);
            }
        }
    }

    @Override
    public void addCookie(String name, String value) {
        addCookie(name, value, null);
    }

    @Override
    public void addCookie(String name, String value, String domain) {
        addCookie(name, value, domain, 0);
    }

    @Override
    public void addCookie(String name, String value, long maxAge) {
        addCookie(name, value, null, maxAge);
    }

    @Override
    public void addCookie(String name, String value, String domain, long maxAge) {
        if (StrUtil.isNotBlank(name) && StrUtil.isNotBlank(value)) {
            Cookie cookie = new DefaultCookie(name, value);
            cookie.setPath("/");
            if (domain != null && domain.trim().length() > 0) {
                cookie.setDomain(domain);
            }
            if (maxAge > 0) {
                cookie.setMaxAge(maxAge);
            }
            setCookie(cookie);
        }
    }

    @Override
    public boolean deleteCookie(String name) {
        Cookie cookie = getCookie(name);
        if (cookie != null) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            setCookie(cookie);
            return true;
        }
        return false;
    }


}
