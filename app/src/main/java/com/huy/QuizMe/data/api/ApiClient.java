package com.huy.QuizMe.data.api;

import com.huy.QuizMe.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Lớp Singleton để quản lý Retrofit API client
 */
public class ApiClient {
    private static final String BASE_URL = BuildConfig.BASE_URL;
    private static ApiClient instance;
    private final Retrofit retrofit;

    private ApiClient() {
        // Tạo logging interceptor để gỡ lỗi
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    /**
     * Lấy instance của CategoryService
     *
     * @return CategoryService
     */
    public CategoryService getCategoryService() {
        return retrofit.create(CategoryService.class);
    }

    /**
     * Lấy instance của QuizService
     *
     * @return QuizService
     */
    public QuizService getQuizService() {
        return retrofit.create(QuizService.class);
    }

    /**
     * Lấy instance của UserService
     *
     * @return UserService
     */
    public UserService getUserService() {
        return retrofit.create(UserService.class);
    }
    
    /**
     * Lấy instance của QuestionService
     *
     * @return QuestionService
     */
    public QuestionService getQuestionService() {
        return retrofit.create(QuestionService.class);
    }


    /**
     * Lấy instance của RoomService
     *
     * @return RoomService
     */
    public RoomService getRoomService() {
        return retrofit.create(RoomService.class);
    }
}