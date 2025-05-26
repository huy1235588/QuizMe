package com.huy.QuizMe.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huy.QuizMe.data.api.ApiClient;
import com.huy.QuizMe.data.api.GameService;
import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.game.GameResultDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository cho các chức năng game
 */
public class GameRepository {
    private final GameService gameService;

    public GameRepository() {
        this.gameService = ApiClient.getInstance().getGameService();
    }

    /**
     * Bắt đầu trò chơi multiplayer
     *
     * @param roomId ID của phòng
     * @return LiveData với Resource chứa kết quả
     */
    public LiveData<Resource<Boolean>> startGame(Long roomId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        gameService.startGame(roomId).enqueue(new Callback<ApiResponse<Boolean>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Boolean>> call,
                                   @NonNull Response<ApiResponse<Boolean>> response) {
                Log.d("GameRepository", "Response: " + response.toString());
                handleResponse(response, result, "Không thể bắt đầu trò chơi");
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Boolean>> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Lấy trạng thái game hiện tại
     *
     * @param roomId ID của phòng
     * @return LiveData với Resource chứa trạng thái
     */
    public LiveData<Resource<String>> getGameStatus(Long roomId) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        gameService.getGameStatus(roomId).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<String>> call, @NonNull Response<ApiResponse<String>> response) {
                handleResponse(response, result, "Không thể lấy trạng thái trò chơi");
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<String>> call, @NonNull Throwable t) {
                handleResponse(null, result, "Lỗi kết nối: " + t.getMessage());
            }
        });

        return result;
    }

    /**
     * Lấy kết quả trò chơi
     *
     * @param roomId ID của phòng
     * @return LiveData với Resource chứa kết quả game
     */
    public LiveData<Resource<GameResultDTO>> getGameResults(Long roomId) {
        MutableLiveData<Resource<GameResultDTO>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        gameService.getGameResults(roomId).enqueue(new Callback<ApiResponse<GameResultDTO>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<GameResultDTO>> call, @NonNull Response<ApiResponse<GameResultDTO>> response) {
                 handleResponse(response, result, "Không thể lấy kết quả trò chơi");
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<GameResultDTO>> call, @NonNull Throwable t) {
               handleResponse(null, result, "Lỗi kết nối: " + t.getMessage());
            }
        });

        return result;
    }

    /**
     * Xử lý phản hồi chung cho các cuộc gọi API
     *
     * @param response            Phản hồi từ API
     * @param liveData            LiveData để cập nhật
     * @param defaultErrorMessage Thông báo lỗi mặc định
     * @param <T>                 Kiểu dữ liệu của phản hồi
     */
    private <T> void handleResponse(
            @NonNull Response<ApiResponse<T>> response,
            @NonNull MutableLiveData<Resource<T>> liveData,
            @NonNull String defaultErrorMessage) {

        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse.isSuccess()) {
                liveData.setValue(Resource.success(apiResponse.getData(), apiResponse.getMessage()));
            } else {
                liveData.setValue(Resource.error(apiResponse.getMessage(), null));
            }
        } else {
            liveData.setValue(Resource.error(defaultErrorMessage, null));
        }
    }
}
