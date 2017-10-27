package com.redant.mvc.user;

import com.redant.core.bean.annotation.Bean;
import com.redant.mappers.UserMapper;
import com.mybatissist.sqlsession.SqlSessionContext;

@Bean(name="userService")
public class UserServiceImpl implements IUserService{

    /**
     * mapper
     */
    private UserMapper mapper = SqlSessionContext.getSqlSession().getMapper(UserMapper.class);

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
