package com.huy.QuizMe.data.model;

import com.google.gson.annotations.SerializedName;

import java.time.Instant;

public class Auth {
    @SerializedName("AccessToken")
    private String AccessToken;

    @SerializedName("AccessTokenExpiry")
    private Instant AccessTokenExpiry;

    @SerializedName("RefreshToken")
    private String RefreshToken;

    @SerializedName("RefreshTokenExpiry")
    private Instant RefreshTokenExpiry;

    @SerializedName("user")
    private User user;

    public Auth(String accessToken, Instant accessTokenExpiry, String refreshToken, Instant refreshTokenExpiry, User user) {
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

    public Instant getAccessTokenExpiry() {
        return AccessTokenExpiry;
    }

    public void setAccessTokenExpiry(Instant accessTokenExpiry) {
        AccessTokenExpiry = accessTokenExpiry;
    }

    public String getRefreshToken() {
        return RefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        RefreshToken = refreshToken;
    }

    public Instant getRefreshTokenExpiry() {
        return RefreshTokenExpiry;
    }

    public void setRefreshTokenExpiry(Instant refreshTokenExpiry) {
        RefreshTokenExpiry = refreshTokenExpiry;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
