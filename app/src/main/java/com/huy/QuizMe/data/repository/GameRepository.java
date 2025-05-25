package com.huy.QuizMe.data.repository;

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
    public LiveData<Resource<String>> startGame(Long roomId) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        gameService.startGame(roomId).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<String>> call, @NonNull Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(Resource.success(apiResponse.getData(), apiResponse.getMessage()));
                    } else {
                        result.setValue(Resource.error(apiResponse.getMessage(), null));
                    }
                } else {
                    result.setValue(Resource.error("Không thể bắt đầu trò chơi", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<String>> call, @NonNull Throwable t) {
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
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(Resource.success(apiResponse.getData(), apiResponse.getMessage()));
                    } else {
                        result.setValue(Resource.error(apiResponse.getMessage(), null));
                    }
                } else {
                    result.setValue(Resource.error("Không thể lấy trạng thái game", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<String>> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
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
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<GameResultDTO> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(Resource.success(apiResponse.getData(), apiResponse.getMessage()));
                    } else {
                        result.setValue(Resource.error(apiResponse.getMessage(), null));
                    }
                } else {
                    result.setValue(Resource.error("Không thể lấy kết quả game", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<GameResultDTO>> call, @NonNull Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
