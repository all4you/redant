package com.redant.demo.mvc.user;

import com.redant.core.bean.annotation.Bean;
import com.xiaoleilu.hutool.util.RandomUtil;

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
    public int selectCount(UserBean bean) {
        if(bean==null){
            bean = new UserBean();
        }
        return RandomUtil.randomInt(10);
    }

}
