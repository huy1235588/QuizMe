package com.huy.QuizMe.data.model;

import com.google.gson.annotations.SerializedName;

public class Auth {
    @SerializedName("AccessToken")
    private String AccessToken;

    @SerializedName("AccessTokenExpiry")
    private String AccessTokenExpiry;

    @SerializedName("RefreshToken")
    private String RefreshToken;

    @SerializedName("RefreshTokenExpiry")
    private String RefreshTokenExpiry;

    @SerializedName("user")
    private User user;

    public Auth(String accessToken, String accessTokenExpiry, String refreshToken, String refreshTokenExpiry, User user) {
        AccessToken = accessToken;
        AccessTokenExpiry = accessTokenExpiry;
        RefreshToken = refreshToken;
        RefreshTokenExpiry = refreshTokenExpiry;
        this.user = user;
    }

    // Getters and Setters

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    public String getAccessTokenExpiry() {
        return AccessTokenExpiry;
    }

    public void setAccessTokenExpiry(String accessTokenExpiry) {
        AccessTokenExpiry = accessTokenExpiry;
    }

    public String getRefreshToken() {
        return RefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        RefreshToken = refreshToken;
    }

    public String getRefreshTokenExpiry() {
        return RefreshTokenExpiry;
    }

    public void setRefreshTokenExpiry(String refreshTokenExpiry) {
        RefreshTokenExpiry = refreshTokenExpiry;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
