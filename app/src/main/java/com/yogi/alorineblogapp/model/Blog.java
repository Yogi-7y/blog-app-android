package com.yogi.alorineblogapp.model;


import com.google.firebase.Timestamp;

public class Blog {
    private String title;
    private String description;
    private String imageUri;
    private String userId;
    private Timestamp timestamp;
    private String username;
    private String category;

    public Blog() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Blog(String title, String description, String imageUri, String userId, Timestamp timestamp, String username, String category ) {
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
        this.userId = userId;
        this.timestamp = timestamp;
        this.username = username;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public String getDecsription() {
//        return description;
//    }
//
//    public void setDecsription(String description) {
//        this.description = description;
//    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
