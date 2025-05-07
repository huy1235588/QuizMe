package com.huy.QuizMe.data.api;

import com.huy.QuizMe.data.model.Auth;
import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.data.model.request.LoginRequest;
import com.huy.QuizMe.data.model.request.RegisterRequest;
import com.huy.QuizMe.data.model.request.TokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    /**
     * Đăng nhập
     *
     * @param loginRequest Thông tin đăng nhập
     * @return Thông tin xác thực
     */
    @POST("/api/auth/login")
    Call<Auth> login(@Body LoginRequest loginRequest);

    /**
     * Đăng xuất
     *
     * @return Kết quả đăng xuất
     */
    @POST("/api/auth/logout")
    Call<Void> logout(@Body TokenRequest tokenRequest);

    /**
     * Đăng ký
     *
     * @param loginRequest Thông tin đăng ký
     * @return Thông tin xác thực
     */
    @POST("/api/auth/register")
    Call<Auth> register(@Body RegisterRequest loginRequest);
}
