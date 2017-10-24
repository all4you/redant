package com.redant.mvc.user;

import com.redant.core.bean.BaseBean;

/**
 * UserBean
 * @author gris.wang
 * @create 2017-10-20
 */
public class UserBean extends BaseBean {

    private Integer id;

    private String userName;

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

}
