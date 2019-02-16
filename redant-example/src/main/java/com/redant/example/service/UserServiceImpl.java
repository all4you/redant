package com.redant.example.service;

import com.redant.core.bean.annotation.Bean;

/**
 * @author houyi.wh
 * @date 2017/12/1
 **/
@Bean(name="userService")
public class UserServiceImpl implements UserService {

    @Override
    public UserBean selectUserInfo(Integer id) {
        UserBean user = new UserBean();
        user.setId(id);
        user.setUserName("逅弈逐码");
        return user;
    }

    @Override
    public int selectCount() {
        return 10;
    }

}
