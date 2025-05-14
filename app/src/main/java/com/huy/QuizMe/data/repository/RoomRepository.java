package com.huy.QuizMe.data.repository;

import android.util.Log;

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
    
    // Error message constants
    private static final String ERROR_LOADING_ROOMS = "Unable to load room list";
    private static final String ERROR_CREATING_ROOM = "Unable to create room";
    private static final String ERROR_UPDATING_ROOM = "Unable to update room";
    private static final String ERROR_JOINING_ROOM = "Unable to join room";
    private static final String ERROR_LEAVING_ROOM = "Unable to leave room";
    private static final String ERROR_STARTING_GAME = "Unable to start game";
    private static final String ERROR_GETTING_ROOM = "Unable to get room details";

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
                handleResponse(response, roomsData, ERROR_LOADING_ROOMS);
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
                handleResponse(response, roomData, ERROR_CREATING_ROOM);
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
                handleResponse(response, roomData, ERROR_UPDATING_ROOM);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Room>> call, @NonNull Throwable t) {
                roomData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return roomData;
    }
    
    /**
     * Tham gia vào một phòng
     *
     * @param roomId ID của phòng
     * @return Đối tượng LiveData với thông tin phòng
     */
    public LiveData<Resource<Room>> joinRoom(Long roomId) {
        MutableLiveData<Resource<Room>> roomData = new MutableLiveData<>();
        roomData.setValue(Resource.loading(null));

        roomService.joinRoom(roomId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Room>> call,
                                  @NonNull Response<ApiResponse<Room>> response) {
                handleResponse(response, roomData, ERROR_JOINING_ROOM);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Room>> call, @NonNull Throwable t) {
                roomData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return roomData;
    }
    
    /**
     * Lấy thông tin chi tiết của phòng
     *
     * @param roomId ID của phòng
     * @return Đối tượng LiveData với thông tin phòng
     */
    public LiveData<Resource<Room>> getRoomById(Long roomId) {
        MutableLiveData<Resource<Room>> roomData = new MutableLiveData<>();
        roomData.setValue(Resource.loading(null));

        roomService.getRoomById(roomId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Room>> call,
                                  @NonNull Response<ApiResponse<Room>> response) {
                handleResponse(response, roomData, ERROR_GETTING_ROOM);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Room>> call, @NonNull Throwable t) {
                roomData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return roomData;
    }
    
    /**
     * Rời khỏi phòng
     *
     * @param roomId ID của phòng
     * @return Đối tượng LiveData với kết quả
     */
    public LiveData<Resource<Boolean>> leaveRoom(Long roomId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        roomService.leaveRoom(roomId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Boolean>> call,
                                  @NonNull Response<ApiResponse<Boolean>> response) {
                handleResponse(response, result, ERROR_LEAVING_ROOM);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Boolean>> call, @NonNull Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }
    
    /**
     * Bắt đầu trò chơi (chỉ chủ phòng)
     *
     * @param roomId ID của phòng
     * @return Đối tượng LiveData với thông tin phòng
     */
    public LiveData<Resource<Room>> startGame(Long roomId) {
        MutableLiveData<Resource<Room>> roomData = new MutableLiveData<>();
        roomData.setValue(Resource.loading(null));

        roomService.startGame(roomId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Room>> call,
                                  @NonNull Response<ApiResponse<Room>> response) {
                handleResponse(response, roomData, ERROR_STARTING_GAME);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Room>> call, @NonNull Throwable t) {
                roomData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return roomData;
    }
    
    /**
     * Xử lý phản hồi chung cho các cuộc gọi API
     * 
     * @param response Phản hồi từ API
     * @param liveData LiveData để cập nhật
     * @param defaultErrorMessage Thông báo lỗi mặc định
     * @param <T> Kiểu dữ liệu của phản hồi
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