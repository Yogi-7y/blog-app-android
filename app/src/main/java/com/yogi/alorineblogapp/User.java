package com.yogi.alorineblogapp;

public class User {

    private String userId;
    private String userame;
    private String phone;

    public User() {}

    public User(String userId, String userame, String phone) {
        this.userId = userId;
        this.userame = userame;
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserame() {
        return userame;
    }

    public void setUserame(String userame) {
        this.userame = userame;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
