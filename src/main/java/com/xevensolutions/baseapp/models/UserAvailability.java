package com.xevensolutions.baseapp.models;

import com.google.gson.annotations.SerializedName;

public class UserAvailability {

    @SerializedName("isOnline")
    boolean isOnline;
    @SerializedName("user")
    int userId;

    public UserAvailability(boolean isOnline, int userId) {
        this.isOnline = isOnline;
        this.userId = userId;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
