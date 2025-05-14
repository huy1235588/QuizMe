package com.huy.QuizMe.data.api;

import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.ChatMessage;
import com.huy.QuizMe.data.model.request.ChatMessageRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface định nghĩa các endpoint API cho chat
 */
public interface ChatService {
    /**
     * Lấy tất cả tin nhắn trong một phòng chat
     *
     * @param roomId ID của phòng chat
     * @return Danh sách tin nhắn
     */
    @GET("/api/chat/room/{roomId}")
    Call<ApiResponse<List<ChatMessage>>> getMessages(@Path("roomId") Long roomId);

    /**
     * Gửi tin nhắn mới vào phòng chat
     *
     * @param messageRequest Thông tin tin nhắn
     * @return Tin nhắn đã gửi
     */
    @RequiresAuth
    @POST("/api/chat/send")
    Call<ApiResponse<ChatMessage>> sendMessage(@Body ChatMessageRequest messageRequest);
}