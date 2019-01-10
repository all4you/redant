package com.redant.example.service.user;

import cn.hutool.core.util.RandomUtil;
import com.redant.core.bean.annotation.Bean;

@Bean(name="userService")
public class UserServiceImpl implements IUserService{


    @Override
    public UserBean selectUserInfo(Integer id) {
        UserBean user = new UserBean();
        user.setId(id);
        user.setUserName("fakeName");
        return user;
    }

    @Override
    public int selectCount() {
        return RandomUtil.randomInt(10);
    }

}
