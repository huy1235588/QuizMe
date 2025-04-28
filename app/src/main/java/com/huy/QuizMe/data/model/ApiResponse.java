package com.huy.QuizMe.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp bọc chung cho các phản hồi từ API
 */
public class ApiResponse<T> {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private T data;

    @SerializedName("message")
    private String message;

    @SerializedName("code")
    private Integer code;

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
}