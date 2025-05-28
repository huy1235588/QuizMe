package com.huy.QuizMe.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huy.QuizMe.data.api.ApiClient;
import com.huy.QuizMe.data.api.UserService;
import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.data.model.UserProfile;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final UserService userService;

    public UserRepository() {
        userService = ApiClient.getInstance().getUserService();
    }

    /**
     * Lấy danh sách người dùng có tổng số quiz được chơi nhiều nhất
     *
     * @return Danh sách người dùng
     */
    public LiveData<Resource<List<User>>> getTopUsers() {
        MutableLiveData<Resource<List<User>>> usersData = new MutableLiveData<>();
        usersData.setValue(Resource.loading(null));

        userService.getTopUsers().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<User>>> call,
                                   @NonNull Response<ApiResponse<List<User>>> response) {
                handleApiResponse(response, usersData, "Failed to fetch top users");
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<User>>> call, @NonNull Throwable t) {
                handleApiFailure(usersData, t);
            }
        });

        return usersData;
    }

    /**
     * Lấy người dùng theo ID
     *
     * @param userId ID của người dùng
     * @return Thông tin người dùng
     */
    public LiveData<Resource<User>> getUserById(Long userId) {
        MutableLiveData<Resource<User>> userData = new MutableLiveData<>();
        userData.setValue(Resource.loading(null));

        userService.getUserById(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call,
                                   @NonNull Response<ApiResponse<User>> response) {
                handleApiResponse(response, userData, "Failed to fetch user by ID");
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                handleApiFailure(userData, t);
            }
        });

        return userData;
    }

    /**
     * Lấy thông tin người dùng hiện tại
     *
     * @return Thông tin người dùng
     */
    public LiveData<Resource<UserProfile>> getCurrentUserProfile() {
        MutableLiveData<Resource<UserProfile>> userData = new MutableLiveData<>();
        userData.setValue(Resource.loading(null));

        userService.getCurrentUserProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UserProfile>> call,
                                   @NonNull Response<ApiResponse<UserProfile>> response) {
                handleApiResponse(response, userData, "Failed to fetch current user profile");
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<UserProfile>> call, @NonNull Throwable t) {
                handleApiFailure(userData, t);
            }
        });

        return userData;
    }

    /**
     * Lấy thông tin người dùng theo ID
     *
     * @param userId ID của người dùng
     * @return Thông tin người dùng
     */
    public LiveData<Resource<UserProfile>> getUserProfileById(Long userId) {
        MutableLiveData<Resource<UserProfile>> userData = new MutableLiveData<>();
        userData.setValue(Resource.loading(null));

        userService.getUserProfileById(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UserProfile>> call,
                                   @NonNull Response<ApiResponse<UserProfile>> response) {
                handleApiResponse(response, userData, "Failed to fetch user profile by ID");
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<UserProfile>> call, @NonNull Throwable t) {
                handleApiFailure(userData, t);
            }
        });

        return userData;
    }

    /**
     * Upload avatar cho người dùng
     *
     * @param avatarFile Tập tin ảnh đại diện
     * @return Thông tin người dùng sau khi cập nhật
     */
    public LiveData<Resource<UserProfile>> uploadAvatar(MultipartBody.Part avatarFile) {
        MutableLiveData<Resource<UserProfile>> userData = new MutableLiveData<>();
        userData.setValue(Resource.loading(null));

        userService.uploadAvatar(avatarFile).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UserProfile>> call,
                                   @NonNull Response<ApiResponse<UserProfile>> response) {
                handleApiResponse(response, userData, "Failed to upload avatar");
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<UserProfile>> call, @NonNull Throwable t) {
                handleApiFailure(userData, t);
            }
        });

        return userData;
    }

    /**
     * Xử lý phản hồi API chung
     *
     * @param response     Phản hồi từ API
     * @param liveData     LiveData để cập nhật kết quả
     * @param errorMessage Thông báo lỗi mặc định
     * @param <T>          Kiểu dữ liệu của dữ liệu trả về
     */
    private <T> void handleApiResponse(
            @NonNull Response<ApiResponse<T>> response,
            @NonNull MutableLiveData<Resource<T>> liveData,
            @NonNull String errorMessage) {

        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                // Kiểm tra thêm đối với List để tránh trả về list rỗng
                if (apiResponse.getData() instanceof List<?> && ((List<?>) apiResponse.getData()).isEmpty()) {
                    liveData.setValue(Resource.error(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "No data available", null));
                } else {
                    liveData.setValue(Resource.success(apiResponse.getData(), apiResponse.getMessage()));
                }
            } else {
                liveData.setValue(Resource.error(apiResponse.getMessage(), null));
            }
        } else {
            liveData.setValue(Resource.error(errorMessage, null));
        }
    }

    /**
     * Xử lý lỗi API chung
     *
     * @param liveData  LiveData để cập nhật kết quả
     * @param throwable Lỗi xảy ra
     * @param <T>       Kiểu dữ liệu của dữ liệu trả về
     */
    private <T> void handleApiFailure(
            @NonNull MutableLiveData<Resource<T>> liveData,
            @NonNull Throwable throwable) {

        String errorMessage = throwable.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Unknown network error occurred";
        }
        liveData.setValue(Resource.error(errorMessage, null));
    }
}
