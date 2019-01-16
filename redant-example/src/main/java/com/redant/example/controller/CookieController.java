package com.redant.example.controller;


import com.alibaba.fastjson.JSONObject;
import com.redant.core.bean.annotation.Bean;
import com.redant.core.common.enums.RequestMethod;
import com.redant.core.controller.annotation.Controller;
import com.redant.core.controller.annotation.Mapping;
import com.redant.core.controller.annotation.Param;
import com.redant.core.cookie.CookieManager;
import com.redant.core.cookie.DefaultCookieManager;
import com.redant.core.render.RenderType;

/**
 * @author houyi.wh
 * @date 2017/12/1
 **/
@Bean
@Controller(path="/cookie")
public class CookieController {

    private CookieManager cookieManager = DefaultCookieManager.getInstance();

    @Mapping(path="/add",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public JSONObject add(@Param(key="name", notBlank=true) String name, @Param(key="value", notBlank=true) String value){
        JSONObject object = new JSONObject();
        object.put("tip","请在响应头 Response Headers 中查看 set-cookie 的值");
        object.put("cookieName",name);
        object.put("cookieValue",value);
        cookieManager.addCookie(name,value);
        return object;
    }

    @Mapping(path="/delete",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public JSONObject delete(@Param(key="name", notBlank=true) String name){
        JSONObject object = new JSONObject();
        object.put("tip","请在响应头 Response Headers 中查看 set-cookie 的值");
        object.put("cookieName",name);
        cookieManager.deleteCookie(name);
        return object;
    }

}
