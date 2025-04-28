package com.huy.QuizMe.data.api;

import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Interface định nghĩa các endpoint API cho danh mục
 */
public interface CategoryService {
    /**
     * Lấy tất cả danh mục
     *
     * @return Danh sách tất cả danh mục
     */
    @GET("/api/categories")
    Call<ApiResponse<List<Category>>> getAllCategories();

    /**
     * Lấy danh mục theo ID
     *
     * @param categoryId ID của danh mục
     * @return Danh mục với ID đã cho
     */
    @GET("/api/categories/{id}")
    Call<ApiResponse<Category>> getCategoryById(@Path("id") int categoryId);

    /**
     * Lấy tất cả danh mục đang hoạt động
     *
     * @return Danh sách tất cả danh mục đang hoạt động
     */
    @GET("/api/categories/active")
    Call<ApiResponse<List<Category>>> getActiveCategories();
}