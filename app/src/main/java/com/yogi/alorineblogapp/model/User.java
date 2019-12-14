package com.yogi.alorineblogapp.model;


import android.app.Application;

import java.io.Serializable;

public class User  {

    private String userId;
    private String username;
    private String phone;
    private static User instance;

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public User() {}

    public User(String userId, String username, String phone) {
        this.userId = userId;
        this.username = username;
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
