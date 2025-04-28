package com.huy.QuizMe.utils;

import androidx.annotation.NonNull;

import com.huy.QuizMe.data.repository.Resource;

import java.io.IOException;

import retrofit2.Response;

/**
 * Lớp tiện ích để xử lý phản hồi API
 */
public class ApiUtils {

    /**
     * Xử lý lỗi API và chuyển đổi thành thông báo lỗi phù hợp
     * @param e Throwable exception
     * @return Thông báo lỗi dễ đọc
     */
    public static String handleApiError(Throwable e) {
        if (e instanceof IOException) {
            return "Unexpected error occurred. Please check your internet connection.";
        }
        return e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.";
    }

    /**
     * Kiểm tra và xử lý mã lỗi phản hồi API
     * @param code Mã phản hồi HTTP
     * @return Thông báo lỗi dễ đọc
     */
    public static String getErrorMessage(int code) {
        switch (code) {
            case 400:
            return "Invalid request.";
            case 401:
            return "You are not logged in or your session has expired.";
            case 403:
            return "You don't have permission to access this content.";
            case 404:
            return "Requested content not found.";
            case 500:
            return "Server error. Please try again later.";
            default:
            return "An error occurred. Error code: " + code;
        }
    }

    /**
     * Kiểm tra Resource có đang ở trạng thái tải không
     * @param resource Resource cần kiểm tra
     * @return true nếu resource đang ở trạng thái tải, false nếu không
     */
    public static <T> boolean isLoading(@NonNull Resource<T> resource) {
        return resource.getStatus() == Resource.Status.LOADING;
    }

    /**
     * Kiểm tra Resource có đang ở trạng thái thành công không
     * @param resource Resource cần kiểm tra
     * @return true nếu resource đang ở trạng thái thành công, false nếu không
     */
    public static <T> boolean isSuccess(@NonNull Resource<T> resource) {
        return resource.getStatus() == Resource.Status.SUCCESS;
    }

    /**
     * Kiểm tra Resource có đang ở trạng thái lỗi không
     * @param resource Resource cần kiểm tra
     * @return true nếu resource đang ở trạng thái lỗi, false nếu không
     */
    public static <T> boolean isError(@NonNull Resource<T> resource) {
        return resource.getStatus() == Resource.Status.ERROR;
    }
}