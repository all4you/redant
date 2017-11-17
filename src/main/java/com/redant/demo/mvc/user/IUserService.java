package com.redant.demo.mvc.user;

public interface IUserService {

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    UserBean selectUserInfo(Integer id);

    int selectCount(UserBean bean);
}
