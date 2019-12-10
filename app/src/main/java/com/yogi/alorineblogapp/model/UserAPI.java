package com.yogi.alorineblogapp.model;

import android.app.Application;

public class UserAPI extends Application {

    private String userId;
    private String username;
    private String phone;
    private static UserAPI instance;

    public static UserAPI getInstance() {
        if (instance == null) {
            instance = new UserAPI();
        }
        return instance;
    }

    public UserAPI() {
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

    public static void setInstance(UserAPI instance) {
        UserAPI.instance = instance;
    }
}
