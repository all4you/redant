package com.redant.mvc.user;

import com.redant.core.bean.BaseBean;
import com.redant.mybatissist.annotation.Table;

/**
 * UserBean
 * @author gris.wang
 * @create 2017-10-20
 */
@Table(name="user_bean",alias="u")
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
