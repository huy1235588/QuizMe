package com.huy.QuizMe.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huy.QuizMe.data.api.ApiClient;
import com.huy.QuizMe.data.api.QuestionService;
import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.Question;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository để xử lý các hoạt động dữ liệu liên quan đến câu hỏi
 */
public class QuestionRepository {
    private final QuestionService questionService;

    /**
     * Constructor khởi tạo repository
     */
    public QuestionRepository() {
        questionService = ApiClient.getInstance().getQuestionService();
    }

    /**
     * Lấy tất cả câu hỏi của một quiz
     *
     * @param quizId ID của quiz
     * @return LiveData chứa danh sách câu hỏi
     */
    public LiveData<Resource<List<Question>>> getAllQuestions(int quizId) {
        MutableLiveData<Resource<List<Question>>> questionsData = new MutableLiveData<>();
        questionsData.setValue(Resource.loading(null));

        questionService.getAllQuestions(quizId).enqueue(new Callback<ApiResponse<List<Question>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Question>>> call, 
                                  @NonNull Response<ApiResponse<List<Question>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Question>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        questionsData.setValue(Resource.success(apiResponse.getData(), 
                                apiResponse.getMessage()));
                    } else {
                        questionsData.setValue(Resource.error(apiResponse.getMessage() != null ? 
                                apiResponse.getMessage() : "Không tìm thấy câu hỏi", null));
                    }
                } else {
                    questionsData.setValue(Resource.error("Không thể tải câu hỏi", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Question>>> call, @NonNull Throwable t) {
                questionsData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return questionsData;
    }

    /**
     * Lấy câu hỏi theo ID
     *
     * @param questionId ID của câu hỏi
     * @return LiveData chứa câu hỏi
     */
    public LiveData<Resource<Question>> getQuestionById(int questionId) {
        MutableLiveData<Resource<Question>> questionData = new MutableLiveData<>();
        questionData.setValue(Resource.loading(null));

        questionService.getQuestionById(questionId).enqueue(new Callback<ApiResponse<Question>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Question>> call, 
                                  @NonNull Response<ApiResponse<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Question> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        questionData.setValue(Resource.success(apiResponse.getData(), 
                                apiResponse.getMessage()));
                    } else {
                        questionData.setValue(Resource.error(apiResponse.getMessage() != null ? 
                                apiResponse.getMessage() : "Không tìm thấy câu hỏi", null));
                    }
                } else {
                    questionData.setValue(Resource.error("Không thể tải câu hỏi", null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Question>> call, @NonNull Throwable t) {
                questionData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return questionData;
    }
}