package com.huy.QuizMe.data.model;

import com.google.gson.annotations.SerializedName;

public class Auth {
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("accessTokenExpiry")
    private String accessTokenExpiry;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("refreshTokenExpiry")
    private String refreshTokenExpiry;

    @SerializedName("user")
    private User user;

    public Auth(String accessToken, String accessTokenExpiry, String refreshToken, String refreshTokenExpiry, User user) {
        accessToken = accessToken;
        accessTokenExpiry = accessTokenExpiry;
        refreshToken = refreshToken;
        refreshTokenExpiry = refreshTokenExpiry;
        this.user = user;
    }

    // Getters and Setters

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenExpiry() {
        return accessTokenExpiry;
    }

    public void setAccessTokenExpiry(String accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshTokenExpiry() {
        return refreshTokenExpiry;
    }

    public void setRefreshTokenExpiry(String refreshTokenExpiry) {
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
