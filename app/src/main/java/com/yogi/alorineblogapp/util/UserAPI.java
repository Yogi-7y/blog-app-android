package com.yogi.alorineblogapp.util;

import android.app.Application;

public class UserAPI extends Application{

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

    public UserAPI() {}

    public UserAPI(String userId, String username, String phone) {
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

    public String getUserame() {
        return username;
    }

    public void setUserame(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
