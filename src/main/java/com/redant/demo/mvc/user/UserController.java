package com.redant.demo.mvc.user;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redant.common.enums.RequestMethod;
import com.redant.core.bean.annotation.Autowired;
import com.redant.core.bean.annotation.Bean;
import com.redant.core.render.*;
import com.redant.core.router.annotation.RouterController;
import com.redant.core.router.annotation.RouterMapping;
import com.redant.core.router.annotation.RouterParam;
import io.netty.handler.codec.http.cookie.DefaultCookie;

@Bean()  // 如果需要使用Autowired，则该类自身需要使用Bean注解标注
@RouterController(path="/user")
public class UserController {

    @Autowired(name="userService")
    private IUserService userService;

    @RouterMapping(path="/info",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public CookieRender getUserInfo(UserBean userBean, @RouterParam(key="pid") Integer pid){
        JSONObject object = new JSONObject();
        object.put("user",userService.selectUserInfo(userBean.getId()));
        object.put("pid",pid);
        CookieRender render = new DefaultCookieRender(RenderType.JSON,object);
        // 设置Cookie
        render.setCookie(new DefaultCookie("ak","47"));
        return render;
    }

    @RouterMapping(path="/list",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public Render getUserList(){
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        UserBean user = new UserBean();
        user.setId(23);
        user.setUserName("wang");
        object.put("user",user);
        array.add(object);
        Render render = new DefaultRender(RenderType.JSON,array);
        return render;
    }

    @RouterMapping(path="/count",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public Render getUserCount(UserBean userBean){
        JSONObject object = new JSONObject();
        int count = userService.selectCount();
        object.put("count",count);
        return new DefaultRender(RenderType.JSON,object);
    }

}
