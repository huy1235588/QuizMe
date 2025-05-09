package com.huy.QuizMe.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huy.QuizMe.data.api.ApiClient;
import com.huy.QuizMe.data.api.ChatService;
import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.ChatMessage;
import com.huy.QuizMe.data.model.request.ChatMessageRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository để xử lý các hoạt động dữ liệu liên quan đến chat
 */
public class ChatRepository {
    private final ChatService chatService;

    // Error message constants
    private static final String ERROR_LOADING_MESSAGES = "Không thể tải tin nhắn";
    private static final String ERROR_SENDING_MESSAGE = "Không thể gửi tin nhắn";

    public ChatRepository() {
        chatService = ApiClient.getInstance().getChatService();
    }

    /**
     * Lấy tin nhắn từ một phòng chat
     *
     * @return LiveData chứa danh sách tin nhắn
     */
    public LiveData<Resource<List<ChatMessage>>> getMessages() {
        MutableLiveData<Resource<List<ChatMessage>>> messagesData = new MutableLiveData<>();
        messagesData.setValue(Resource.loading(null));

        chatService.getMessages().enqueue(new Callback<ApiResponse<List<ChatMessage>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<ChatMessage>>> call,
                                   @NonNull Response<ApiResponse<List<ChatMessage>>> response) {
                handleResponse(response, messagesData, ERROR_LOADING_MESSAGES);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<ChatMessage>>> call, @NonNull Throwable t) {
                messagesData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return messagesData;
    }

    /**
     * Gửi tin nhắn vào phòng chat
     *
     * @param roomId  ID của phòng chat
     * @param message Tin nhắn cần gửi
     * @return LiveData chứa tin nhắn đã gửi
     */
    public LiveData<Resource<ChatMessage>> sendMessage(Long roomId, ChatMessage message) {
        MutableLiveData<Resource<ChatMessage>> messageData = new MutableLiveData<>();
        messageData.setValue(Resource.loading(null));

        // Tạo đối tượng ChatMessageRequest từ thông tin tin nhắn
        ChatMessageRequest messageRequest = new ChatMessageRequest(roomId, message.getMessage(), message.getGuestName());

        // Gửi yêu cầu gửi tin nhắn
        chatService.sendMessage(messageRequest).enqueue(new Callback<ApiResponse<ChatMessage>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ChatMessage>> call,
                                   @NonNull Response<ApiResponse<ChatMessage>> response) {
                handleResponse(response, messageData, ERROR_SENDING_MESSAGE);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ChatMessage>> call, @NonNull Throwable t) {
                messageData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return messageData;
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