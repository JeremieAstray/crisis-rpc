package com.jeremie.spring.home.entity;

import com.jeremie.spring.entity.BaseEntity;

import javax.persistence.*;

/**
 * @author guanhong 15/8/18 下午4:07.
 */
@Entity
@Table(name = "user")
public class User extends BaseEntity {

    @Column
    private String username;
    @Column
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
