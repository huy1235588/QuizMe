package com.huy.QuizMe.data.model.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("usernameOrEmail")
    private String usernameOrEmail;

    @SerializedName("password")
    private String password;

    public LoginRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    // Getters and Setters

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
