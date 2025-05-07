package com.huy.QuizMe.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huy.QuizMe.data.api.ApiClient;
import com.huy.QuizMe.data.api.RoomService;
import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.model.request.RoomRequest;

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

    /**
     * Tạo phòng mới
     *
     * @param roomRequest Thông tin phòng
     * @return Đối tượng LiveData với thông tin phòng vừa tạo
     */
    public LiveData<Resource<Room>> createRoom(RoomRequest roomRequest) {
        MutableLiveData<Resource<Room>> roomData = new MutableLiveData<>();
        roomData.setValue(Resource.loading(null));

        roomService.createRoom(roomRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Room>> call,
                                   @NonNull Response<ApiResponse<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Room> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        roomData.setValue(Resource.success(apiResponse.getData(), apiResponse.getMessage()));
                    } else {
                        roomData.setValue(Resource.error(apiResponse.getMessage(), null));
                    }
                } else {
                    roomData.setValue(Resource.error("Không thể tạo phòng", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Room>> call, @NonNull Throwable t) {
                roomData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return roomData;
    }

    /**
     * Cập nhật thông tin phòng
     *
     * @param roomId      ID của phòng
     * @param roomRequest Thông tin phòng mới
     * @return Đối tượng LiveData với thông tin phòng đã cập nhật
     */
    public LiveData<Resource<Room>> updateRoom(Long roomId, RoomRequest roomRequest) {
        MutableLiveData<Resource<Room>> roomData = new MutableLiveData<>();
        roomData.setValue(Resource.loading(null));

        roomService.updateRoom(roomId, roomRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Room>> call,
                                   @NonNull Response<ApiResponse<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Room> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        roomData.setValue(Resource.success(apiResponse.getData(), apiResponse.getMessage()));
                    } else {
                        roomData.setValue(Resource.error(apiResponse.getMessage(), null));
                    }
                } else {
                    roomData.setValue(Resource.error("Không thể cập nhật phòng", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Room>> call, @NonNull Throwable t) {
                roomData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return roomData;
    }
}