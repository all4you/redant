package com.redant.mvc.user;

import com.redant.core.bean.annotation.Bean;
import com.redant.mappers.UserMapper;
import com.redant.mybatissist.mapper.Mapper;
import com.redant.mybatissist.sqlsession.SqlSessionContext;

@Bean(name="userService")
public class UserServiceImpl implements IUserService{

    /**
     * mapper
     */
    private Mapper mapper = SqlSessionContext.getSqlSession().getMapper(UserMapper.class);

    @Override
    public UserBean selectUserInfo(Integer id) {
        UserBean user = new UserBean();
        user.setId(id);
        return (UserBean)mapper.selectOne(UserBean.class,user);
    }

    @Override
    public int selectCount(UserBean bean) {
        if(bean==null){
            bean = new UserBean();
        }
        return mapper.selectCount(UserBean.class,bean);
    }

}
