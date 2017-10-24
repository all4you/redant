package com.redant.mvc.user;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redant.core.bean.annotation.Autowired;
import com.redant.core.bean.annotation.Bean;
import com.redant.core.enums.RequestMethod;
import com.redant.core.render.BaseRender;
import com.redant.core.render.RenderType;
import com.redant.core.router.annotation.RouterController;
import com.redant.core.router.annotation.RouterMapping;
import com.redant.core.router.annotation.RouterParam;

@Bean()  // 如果需要使用Autowired，则该类自身需要使用Bean注解标注
@RouterController(path="/UserController")
public class UserController {

    @Autowired(name="userService")
    private IUserService userService;

    @RouterMapping(path="/getUserInfo",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public BaseRender getUserInfo(UserBean userBean,@RouterParam(key="pid") Integer pid){
        JSONObject object = new JSONObject();
        object.put("user",userService.getUserInfo(34));
        object.put("pid",pid);
        return new BaseRender(RenderType.JSON,object);
    }

    @RouterMapping(path="/getUserList",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public BaseRender getUserList(){
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        UserBean user = new UserBean();
        user.setId(23);
        user.setUserName("wang");
        object.put("user",user);
        array.add(object);
        return new BaseRender(RenderType.JSON,array);
    }

}
