package com.redant.mvc.user;


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
@RouterController(path="/UserController")
public class UserController {

    @Autowired(name="userService")
    private IUserService userService;

    @RouterMapping(path="/getUserInfo",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public CookieRender getUserInfo(UserBean userBean, @RouterParam(key="pid") Integer pid){
        JSONObject object = new JSONObject();
        object.put("user",userService.selectUserInfo(userBean.getId()));
        object.put("pid",pid);
        CookieRender render = new BaseCookieRender(RenderType.JSON,object);
        // 设置Cookie
        render.setCookie(new DefaultCookie("ak","47"));
        return render;
    }

    @RouterMapping(path="/getUserList",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public CookieRender getUserList(){
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        UserBean user = new UserBean();
        user.setId(23);
        user.setUserName("wang");
        object.put("user",user);
        array.add(object);
        CookieRender render = new BaseCookieRender(RenderType.JSON,array);
        // 设置Cookie
        render.setCookie(new DefaultCookie("ak","47"));
        return render;
    }

    @RouterMapping(path="/getUserCount",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public Render getUserCount(UserBean userBean){
        JSONObject object = new JSONObject();
        int count = userService.selectCount(userBean);
        object.put("count",count);
        return new BaseRender(RenderType.JSON,object);
    }

}
