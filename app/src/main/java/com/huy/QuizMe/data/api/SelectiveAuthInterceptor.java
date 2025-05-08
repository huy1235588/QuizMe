package com.huy.QuizMe.data.api;

import androidx.annotation.NonNull;

import com.huy.QuizMe.utils.SharedPreferencesManager;

import java.io.IOException;
import java.lang.reflect.Method;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Invocation;

/**
 * Interceptor để chỉ thêm JWT token vào header của các request được đánh dấu bằng @RequiresAuth
 */
public class SelectiveAuthInterceptor implements Interceptor {
    private final SharedPreferencesManager prefsManager;

    public SelectiveAuthInterceptor() {
        this.prefsManager = SharedPreferencesManager.getInstance();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        // Lấy thông tin của phương thức Retrofit đang gọi request
        Invocation invocation = request.tag(Invocation.class);
        if (invocation != null) {
            Method method = invocation.method();
            // Kiểm tra xem phương thức này có được đánh dấu bằng @RequiresAuth không
            RequiresAuth requiresAuth = method.getAnnotation(RequiresAuth.class);

            // Nếu phương thức được đánh dấu @RequiresAuth, thêm token vào header
            if (requiresAuth != null) {
                String token = prefsManager.getAuthToken();

                if (token != null && !token.isEmpty()) {
                    request = request.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .build();
                }
            }
        }

        return chain.proceed(request);
    }
}