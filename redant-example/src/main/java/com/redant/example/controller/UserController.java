package com.redant.example.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redant.core.bean.annotation.Autowired;
import com.redant.core.bean.annotation.Bean;
import com.redant.core.common.enums.RequestMethod;
import com.redant.core.render.RenderType;
import com.redant.core.router.annotation.RouterController;
import com.redant.core.router.annotation.RouterMapping;
import com.redant.core.router.annotation.RouterParam;
import com.redant.example.service.user.IUserService;
import com.redant.example.service.user.UserBean;

/**
 * @author gris.wang
 * @date 2017/12/1
 **/
@Bean
@RouterController(path="/user")
public class UserController {

    /**
     * 如果需要使用Autowired，则该类自身需要使用Bean注解标注
     */
    @Autowired(name="userService")
    private IUserService userService;

    @RouterMapping(path="/info",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public UserBean getUserInfo(@RouterParam(key="id",checkNull=true) Integer id){
        return userService.selectUserInfo(id);
    }

    @RouterMapping(path="/list",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public JSONArray getUserList(){
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        UserBean user = new UserBean();
        user.setId(23);
        user.setUserName("wang");
        object.put("user",user);
        array.add(object);
        return array;
    }

    @RouterMapping(path="/count",requestMethod=RequestMethod.GET,renderType=RenderType.JSON)
    public JSONObject getUserCount(){
        JSONObject object = new JSONObject();
        int count = userService.selectCount();
        object.put("count",count);
        return object;
    }

}
