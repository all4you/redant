package com.redant.example.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redant.core.bean.annotation.Autowired;
import com.redant.core.bean.annotation.Bean;
import com.redant.core.common.enums.RequestMethod;
import com.redant.core.cookie.CookieManager;
import com.redant.core.cookie.DefaultCookieManager;
import com.redant.core.render.RenderType;
import com.redant.core.controller.annotation.Controller;
import com.redant.core.controller.annotation.Mapping;
import com.redant.core.controller.annotation.Param;
import com.redant.example.service.user.IUserService;
import com.redant.example.service.user.UserBean;

/**
 * @author houyi.wh
 * @date 2017/12/1
 **/
@Bean
@Controller(path="/user")
public class UserController {

    /**
     * 如果需要使用Autowired，则该类自身需要使用Bean注解标注
     */
    @Autowired(name="userService")
    private IUserService userService;

    private CookieManager cookieManager = DefaultCookieManager.getInstance();

    @Mapping(path="/info",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public UserBean getUserInfo(@Param(key="id", notNull=true) Integer id){
        return userService.selectUserInfo(id);
    }

    @Mapping(path="/list",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public JSONArray getUserList(){
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        UserBean user = new UserBean();
        user.setId(23);
        user.setUserName("逅弈逐码");
        object.put("user",user);
        array.add(object);
        return array;
    }

    @Mapping(path="/count",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public JSONObject getUserCount(){
        JSONObject object = new JSONObject();
        int count = userService.selectCount();
        object.put("count",count);
        return object;
    }

    @Mapping(path="/cookie",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public JSONObject cookie(@Param(key="name", notBlank=true) String name, @Param(key="value", notBlank=true) String value){
        JSONObject object = new JSONObject();
        object.put("tip","请在响应头 Response Headers 中查看 set-cookie 的值");
        object.put("cookieName",name);
        object.put("cookieValue",value);
        cookieManager.addCookie(name,value);
        return object;
    }

}
