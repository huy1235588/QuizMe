package com.huy.QuizMe.data.api;

import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.User;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.Call;

public interface UserService {
    /**
     * Lấy danh sách người dùng có tổng số quiz được chơi nhiều nhất
     *
     * @return Danh sách người dùng
     */
    @GET("/api/users/top")
    Call<ApiResponse<List<User>>> getTopUsers();

}
