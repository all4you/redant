package com.redant.core.render;

import com.redant.core.DataHolder;
import com.redant.core.cookie.CookieHelper;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;

/**
 * 可以处理Cookie的渲染类
 * @author gris.wang
 * @date 2017/11/15
 **/
public class DefaultCookieRender implements CookieRender {


    @Override
    public void setCookie(Cookie cookie) {
//        if(response==null){
//            response = response();
//        }
//        CookieHelper.setCookie(response,cookie);
    }


    @Override
    public boolean deleteCookie(String name) {
//        if(response==null){
//            response = response();
//        }
//        HttpRequest request = DataHolder.getHttpRequest();
//        return CookieHelper.deleteCookie(request,response,name);
        return true;
    }

}
