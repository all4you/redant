package com.redant.example.service.user;

/**
 * @author gris.wang
 * @date 2017/12/1
 **/
public interface IUserService {

    /**
     * 获取用户信息
     * @param id 用户id
     * @return 用户信息
     */
    UserBean selectUserInfo(Integer id);

    /**
     * 获取用户个数
     * @return 用户个数
     */
    int selectCount();
}
