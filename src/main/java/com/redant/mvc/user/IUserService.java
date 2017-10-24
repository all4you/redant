package com.redant.mvc.user;

public interface IUserService {

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    UserBean getUserInfo(Integer id);

}
