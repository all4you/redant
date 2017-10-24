package com.redant.mvc.user;

import com.redant.core.bean.annotation.Bean;

@Bean(name="userService")
public class UserServiceImpl implements IUserService{

    @Override
    public UserBean getUserInfo(Integer id) {
        UserBean user = new UserBean();
        user.setId(id);
        user.setUserName("fakeName");
        return user;
    }


}
