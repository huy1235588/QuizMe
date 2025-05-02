package com.huy.QuizMe.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huy.QuizMe.data.api.ApiClient;
import com.huy.QuizMe.data.api.UserService;
import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.User;

import java.util.List;

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
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<User>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                        usersData.setValue(Resource.success(apiResponse.getData(), apiResponse.getMessage()));
                    } else {
                        usersData.setValue(Resource.error(apiResponse.getMessage(), null));
                    }
                } else {
                    usersData.setValue(Resource.error("Failed to fetch top users", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<User>>> call, @NonNull Throwable t) {
                usersData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return usersData;
    }

}
