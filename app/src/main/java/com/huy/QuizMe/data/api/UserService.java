package com.huy.QuizMe.data.api;

import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.data.model.UserProfile;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserService {
    /**
     * Lấy danh sách người dùng có tổng số quiz được chơi nhiều nhất
     *
     * @return Danh sách người dùng
     */
    @GET("/api/users/top")
    Call<ApiResponse<List<User>>> getTopUsers();

    /**
     * Lấy người dùng theo id
     *
     * @param userId ID của người dùng
     * @return Thông tin người dùng
     */
    @GET("/api/users/{userId}")
    Call<ApiResponse<User>> getUserById(@Path("userId") Long userId);

    /**
     * Lấy thông tin người dùng hiện tại
     *
     * @return Thông tin người dùng
     */
    @RequiresAuth
    @GET("/api/users/profile")
    Call<ApiResponse<UserProfile>> getCurrentUserProfile();

    /**
     * Lấy thông tin người dùng theo ID
     *
     * @param userId ID của người dùng
     * @return Thông tin người dùng
     */
    @GET("/api/users/profile/{userId}")
    Call<ApiResponse<UserProfile>> getUserProfileById(@Path("userId") Long userId);

    /**
     * Upload avatar cho người dùng
     *
     * @param avatarFile Tập tin ảnh đại diện
     * @return Thông tin người dùng sau khi cập nhật
     */
    @RequiresAuth
    @POST("/api/users/avatar/upload")
    @Multipart
    Call<ApiResponse<UserProfile>> uploadAvatar(@Part MultipartBody.Part avatarFile);
}
