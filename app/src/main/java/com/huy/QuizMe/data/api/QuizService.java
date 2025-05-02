package com.huy.QuizMe.data.api;

import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.PagedResponse;
import com.huy.QuizMe.data.model.Quiz;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface định nghĩa các endpoint API cho quiz
 */
public interface QuizService {
    /**
     * Lấy tất cả quiz với tùy chọn phân trang và lọc
     *
     * @param page       Số trang (bắt đầu từ 0)
     * @param pageSize   Số lượng item trên mỗi trang
     * @param category   Lọc theo ID danh mục
     * @param difficulty Lọc theo độ khó (EASY, MEDIUM, HARD)
     * @param search     Từ khóa tìm kiếm cho tiêu đề và mô tả
     * @param sort       Tùy chọn sắp xếp (newest, popular)
     * @param isPublic   Lọc theo trạng thái công khai
     * @param tab        Lọc theo tab (newest, popular)
     * @return Danh sách phân trang của quiz
     */
    @GET("/api/quizzes")
    Call<ApiResponse<PagedResponse<Quiz>>> getAllQuizzes(
            @Query("page") Integer page,
            @Query("pageSize") Integer pageSize,
            @Query("category") Integer category,
            @Query("difficulty") String difficulty,
            @Query("search") String search,
            @Query("sort") String sort,
            @Query("isPublic") Boolean isPublic,
            @Query("tab") String tab
    );

    /**
     * Lấy quiz phân trang
     *
     * @param page       Số trang (bắt đầu từ 0)
     * @param pageSize   Số lượng item trên mỗi trang
     * @param category   Lọc theo ID danh mục
     * @param difficulty Lọc theo độ khó (EASY, MEDIUM, HARD)
     * @param search     Từ khóa tìm kiếm cho tiêu đề và mô tả
     * @param sort       Tùy chọn sắp xếp (newest, popular)
     * @param isPublic   Lọc theo trạng thái công khai
     * @param tab        Lọc theo tab (newest, popular)
     * @return Danh sách phân trang của quiz
     */
    @GET("/api/quizzes/paged")
    Call<ApiResponse<PagedResponse<Quiz>>> getPagedQuizzes(
            @Query("page") Integer page,
            @Query("pageSize") Integer pageSize,
            @Query("category") Integer category,
            @Query("difficulty") String difficulty,
            @Query("search") String search,
            @Query("sort") String sort,
            @Query("isPublic") Boolean isPublic,
            @Query("tab") String tab
    );

    /**
     * Lấy quiz theo ID
     *
     * @param quizId ID của quiz
     * @return Quiz với ID đã cho
     */
    @GET("/api/quizzes/{id}")
    Call<ApiResponse<List<Quiz>>> getQuizById(@Path("id") int quizId);
}