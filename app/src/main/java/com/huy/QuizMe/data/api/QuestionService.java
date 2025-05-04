package com.huy.QuizMe.data.api;

import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.Question;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QuestionService {
    /**
     * Lấy tất cả câu hỏi của một quiz
     *
     * @param quizId ID của quiz
     * @return Danh sách câu hỏi
     */
    @GET("/api/questions/quiz/{quizId}")
    Call<ApiResponse<List<Question>>> getAllQuestions(@Path("quizId") int quizId);

    /**
     * Lấy câu hỏi theo ID
     *
     * @param questionId ID của câu hỏi
     * @return Câu hỏi
     */
    @GET("/api/questions/{questionId}")
    Call<ApiResponse<Question>> getQuestionById(@Path("questionId") int questionId);
}
