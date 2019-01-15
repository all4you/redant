package com.redant.example.service.user;


import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * UserBean
 * @author houyi.wh
 * @date 2017-10-20
 */
public class UserBean implements Serializable {

    private Integer id;

    private String userName;

    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
