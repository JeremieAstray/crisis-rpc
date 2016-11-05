package com.jeremie.spring.adol.entity;

import com.jeremie.spring.entity.BaseEntity;


/**
 * @author guanhong 15/8/18 下午4:07.
 */
public class User extends BaseEntity {

    private String username;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
