package com.huy.QuizMe.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huy.QuizMe.data.api.ApiClient;
import com.huy.QuizMe.data.api.RoomService;
import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.Room;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository để xử lý các hoạt động dữ liệu liên quan đến phòng quiz
 */
public class RoomRepository {
    private final RoomService roomService;

    public RoomRepository() {
        roomService = ApiClient.getInstance().getRoomService();
    }

    /**
     * Lấy danh sách phòng có thể tham gia từ API
     *
     * @param categoryId ID của danh mục (tùy chọn)
     * @param search     Từ khóa tìm kiếm (tùy chọn)
     * @return Đối tượng LiveData với danh sách phòng
     */
    public LiveData<Resource<List<Room>>> getAvailableRooms(Long categoryId, String search) {
        MutableLiveData<Resource<List<Room>>> roomsData = new MutableLiveData<>();
        roomsData.setValue(Resource.loading(null));

        roomService.getAvailableRooms(categoryId, search).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Room>>> call,
                                   @NonNull Response<ApiResponse<List<Room>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Room>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        roomsData.setValue(Resource.success(apiResponse.getData(), apiResponse.getMessage()));
                    } else {
                        roomsData.setValue(Resource.error(apiResponse.getMessage(), null));
                    }
                } else {
                    roomsData.setValue(Resource.error("Không thể tải danh sách phòng", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Room>>> call, @NonNull Throwable t) {
                roomsData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return roomsData;
    }
}