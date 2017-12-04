package com.redant.defaultcontroller.user;


/**
 * UserBean
 * @author gris.wang
 * @create 2017-10-20
 */
public class UserBean {

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
        return new StringBuffer().append("{")
                                 .append("\"id\":\"").append(this.id)
                                 .append("\",\"userName\":\"").append(this.userName)
                                 .append("\",\"password\":\"").append(this.password)
                                 .append("}").toString();
    }
}
