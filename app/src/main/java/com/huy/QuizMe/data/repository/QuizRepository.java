package com.huy.QuizMe.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huy.QuizMe.data.api.ApiClient;
import com.huy.QuizMe.data.api.QuizService;
import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.PagedResponse;
import com.huy.QuizMe.data.model.Quiz;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository để xử lý các hoạt động dữ liệu liên quan đến quiz
 */
public class QuizRepository {
    private final QuizService quizService;

    public QuizRepository() {
        quizService = ApiClient.getInstance().getQuizService();
    }

    /**
     * Lấy tất cả quiz từ API với các tùy chọn phân trang và lọc
     *
     * @param page       Số trang (dựa trên 0)
     * @param pageSize   Số lượng item trên mỗi trang
     * @param categoryId Lọc theo ID danh mục (tùy chọn)
     * @param difficulty Lọc theo mức độ khó (tùy chọn)
     * @param search     Từ khóa tìm kiếm (tùy chọn)
     * @param sort       Tùy chọn sắp xếp (tùy chọn)
     * @param isPublic   Lọc theo trạng thái công khai (tùy chọn)
     * @param tab        Lọc theo tab (tùy chọn)
     * @return Đối tượng LiveData với phản hồi phân trang của quiz
     */
    public LiveData<Resource<PagedResponse<Quiz>>> getAllQuizzes(
            Integer page,
            Integer pageSize,
            Integer categoryId,
            String difficulty,
            String search,
            String sort,
            Boolean isPublic,
            String tab) {

        MutableLiveData<Resource<PagedResponse<Quiz>>> quizzesData = new MutableLiveData<>();
        quizzesData.setValue(Resource.loading(null));

        quizService.getAllQuizzes(page, pageSize, categoryId, difficulty, search, sort, isPublic, tab)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<PagedResponse<Quiz>>> call,
                                           @NonNull Response<ApiResponse<PagedResponse<Quiz>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<PagedResponse<Quiz>> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                quizzesData.setValue(Resource.success(apiResponse.getData(),
                                        apiResponse.getMessage()));
                            } else {
                                quizzesData.setValue(Resource.error(apiResponse.getMessage(), null));
                            }
                        } else {
                            quizzesData.setValue(Resource.error("Failed to load quizzes", null));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<PagedResponse<Quiz>>> call, @NonNull Throwable t) {
                        quizzesData.setValue(Resource.error(t.getMessage(), null));
                    }
                });

        return quizzesData;
    }

    /**
     * Lấy quiz theo ID
     *
     * @param quizId ID của quiz cần lấy
     * @return Đối tượng LiveData với quiz
     */
    public LiveData<Resource<Quiz>> getQuizById(int quizId) {
        MutableLiveData<Resource<Quiz>> quizData = new MutableLiveData<>();
        quizData.setValue(Resource.loading(null));

        quizService.getQuizById(quizId).enqueue(new Callback<ApiResponse<Quiz>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Quiz>> call, @NonNull Response<ApiResponse<Quiz>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Quiz> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Take the first quiz from the list
                        Quiz quiz = apiResponse.getData();
                        quizData.setValue(Resource.success(quiz, apiResponse.getMessage()));
                    } else {
                        quizData.setValue(Resource.error(apiResponse.getMessage() != null ?
                                apiResponse.getMessage() : "Quiz not found", null));
                    }
                } else {
                    quizData.setValue(Resource.error("Failed to load quiz", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Quiz>> call, @NonNull Throwable t) {
                quizData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return quizData;
    }

    /**
     * Lấy quiz phân trang
     *
     * @param page       Số trang (dựa trên 0)
     * @param pageSize   Số lượng item trên mỗi trang
     * @param categoryId Lọc theo ID danh mục (tùy chọn)
     * @param difficulty Lọc theo mức độ khó (tùy chọn)
     * @param search     Từ khóa tìm kiếm (tùy chọn)
     * @param sort       Tùy chọn sắp xếp (tùy chọn)
     * @param isPublic   Lọc theo trạng thái công khai (tùy chọn)
     * @param tab        Lọc theo tab (tùy chọn)
     * @return Đối tượng LiveData với phản hồi phân trang của quiz
     */
    public LiveData<Resource<PagedResponse<Quiz>>> getPagedQuizzes(
            Integer page,
            Integer pageSize,
            Integer categoryId,
            String difficulty,
            String search,
            String sort,
            Boolean isPublic,
            String tab
    ) {
        MutableLiveData<Resource<PagedResponse<Quiz>>> quizzesData = new MutableLiveData<>();
        quizzesData.setValue(Resource.loading(null));

        quizService.getPagedQuizzes(
                        page,
                        pageSize,
                        categoryId,
                        difficulty,
                        search,
                        sort,
                        isPublic,
                        tab
                )
                .enqueue(new Callback<ApiResponse<PagedResponse<Quiz>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<PagedResponse<Quiz>>> call,
                                           @NonNull Response<ApiResponse<PagedResponse<Quiz>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<PagedResponse<Quiz>> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                quizzesData.setValue(Resource.success(apiResponse.getData(),
                                        apiResponse.getMessage()));
                            } else {
                                quizzesData.setValue(Resource.error(apiResponse.getMessage(), null));
                            }
                        } else {
                            quizzesData.setValue(Resource.error("Failed to load paged quizzes", null));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<PagedResponse<Quiz>>> call, @NonNull Throwable t) {
                        quizzesData.setValue(Resource.error(t.getMessage(), null));
                    }
                });

        return quizzesData;
    }
}