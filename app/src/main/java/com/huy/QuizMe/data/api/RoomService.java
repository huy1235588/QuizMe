package com.huy.QuizMe.data.api;

import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.model.request.RoomRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface định nghĩa các endpoint API cho phòng quiz
 */
public interface RoomService {
    /**
     * Lấy danh sách phòng có thể tham gia
     *
     * @param categoryId ID của danh mục (tùy chọn)
     * @param search     Từ khóa tìm kiếm (tùy chọn)
     * @return Danh sách phòng
     */
    @GET("/api/rooms/available")
    Call<ApiResponse<List<Room>>> getAvailableRooms(
            @Query("categoryId") Long categoryId,
            @Query("search") String search);

    /**
     * Lấy danh sách phòng đang chờ
     *
     * @return Danh sách phòng đang chờ
     */
    @GET("/api/rooms/waiting")
    Call<ApiResponse<List<Room>>> getWaitingRooms();

    /**
     * Lấy thông tin phòng theo mã
     *
     * @param code Mã phòng
     * @return Thông tin phòng
     */
    @GET("/api/rooms/{code}")
    Call<ApiResponse<Room>> getRoomByCode(@Path("code") String code);

    /**
     * Tạo phòng mới
     *
     * @param roomRequest Thông tin phòng
     * @return Thông tin phòng vừa tạo
     */
    @POST("/api/rooms")
    Call<ApiResponse<Room>> createRoom(@retrofit2.http.Body RoomRequest roomRequest);

    /**
     * Cập nhật thông tin phòng
     *
     * @param roomId      ID của phòng
     * @param roomRequest Thông tin phòng mới
     * @return Thông tin phòng đã cập nhật
     */
    @PATCH("/api/rooms/{roomId}")
    Call<ApiResponse<Room>> updateRoom(@Path("roomId") Long roomId, @retrofit2.http.Body RoomRequest roomRequest);
}