package com.redant.demo.mvc.user;

import com.mybatissist.util.MapperUtil;
import com.redant.core.bean.annotation.Bean;
import com.redant.demo.mappers.UserMapper;

@Bean(name="userService")
public class UserServiceImpl implements IUserService{

    /**
     * mapper
     */
    private UserMapper mapper = MapperUtil.getMapper(UserMapper.class);

    @Override
    public UserBean selectUserInfo(Integer id) {
        UserBean user = new UserBean();
        user.setId(id);
        return mapper.selectOne(user,UserBean.class);
    }

    @Override
    public int selectCount(UserBean bean) {
        if(bean==null){
            bean = new UserBean();
        }
        return mapper.selectCount(bean,UserBean.class);
    }

}
