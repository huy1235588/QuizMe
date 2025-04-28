package com.huy.QuizMe.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huy.QuizMe.data.api.ApiClient;
import com.huy.QuizMe.data.api.CategoryService;
import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository để xử lý các hoạt động dữ liệu liên quan đến danh mục
 */
public class CategoryRepository {
    private final CategoryService categoryService;

    public CategoryRepository() {
        categoryService = ApiClient.getInstance().getCategoryService();
    }

    /**
     * Lấy tất cả danh mục từ API
     *
     * @return Đối tượng LiveData với danh sách danh mục
     */
    public LiveData<Resource<List<Category>>> getAllCategories() {
        MutableLiveData<Resource<List<Category>>> categoriesData = new MutableLiveData<>();
        categoriesData.setValue(Resource.loading(null));

        categoryService.getAllCategories().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Category>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        categoriesData.setValue(Resource.success(apiResponse.getData(), apiResponse.getMessage()));
                    } else {
                        categoriesData.setValue(Resource.error(apiResponse.getMessage(), null));
                    }
                } else {
                    categoriesData.setValue(Resource.error("Failed to load categories", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Throwable t) {
                categoriesData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return categoriesData;
    }

    /**
     * Lấy danh mục theo ID
     *
     * @param categoryId ID của danh mục cần lấy
     * @return Đối tượng LiveData với danh mục
     */
    public LiveData<Resource<Category>> getCategoryById(int categoryId) {
        MutableLiveData<Resource<Category>> categoryData = new MutableLiveData<>();
        categoryData.setValue(Resource.loading(null));

        categoryService.getCategoryById(categoryId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Category>> call, @NonNull Response<ApiResponse<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Category> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        categoryData.setValue(Resource.success(apiResponse.getData(), apiResponse.getMessage()));
                    } else {
                        categoryData.setValue(Resource.error(apiResponse.getMessage(), null));
                    }
                } else {
                    categoryData.setValue(Resource.error("Failed to load category", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Category>> call, @NonNull Throwable t) {
                categoryData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return categoryData;
    }

    /**
     * Lấy tất cả danh mục đang hoạt động từ API
     *
     * @return Đối tượng LiveData với danh sách danh mục đang hoạt động
     */
    public LiveData<Resource<List<Category>>> getActiveCategories() {
        MutableLiveData<Resource<List<Category>>> categoriesData = new MutableLiveData<>();
        categoriesData.setValue(Resource.loading(null));

        categoryService.getActiveCategories().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Category>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        categoriesData.setValue(Resource.success(apiResponse.getData(), apiResponse.getMessage()));
                    } else {
                        categoriesData.setValue(Resource.error(apiResponse.getMessage(), null));
                    }
                } else {
                    categoriesData.setValue(Resource.error("Failed to load active categories", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Category>>> call, @NonNull Throwable t) {
                categoriesData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return categoriesData;
    }
}