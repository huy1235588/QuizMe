package com.huy.QuizMe.data.model.request;

import com.google.gson.annotations.SerializedName;

public class TokenRequest {
    @SerializedName("refreshToken")
    private String refreshToken;

    public TokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
