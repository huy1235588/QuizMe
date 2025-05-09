package com.huy.QuizMe.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huy.QuizMe.data.api.ApiClient;
import com.huy.QuizMe.data.api.AuthService;
import com.huy.QuizMe.data.model.ApiResponse;
import com.huy.QuizMe.data.model.Auth;
import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.data.model.request.LoginRequest;
import com.huy.QuizMe.data.model.request.RegisterRequest;
import com.huy.QuizMe.data.model.request.TokenRequest;
import com.huy.QuizMe.utils.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final AuthService authService;
    private final SharedPreferencesManager prefsManager;

    // Error message constants
    private static final String ERROR_LOGIN = "Login failed";
    private static final String ERROR_REGISTER = "Registration failed";
    private static final String ERROR_LOGOUT = "Logout failed";

    public AuthRepository() {
        this.authService = ApiClient.getInstance().getAuthService();
        this.prefsManager = SharedPreferencesManager.getInstance();
    }

    /**
     * Đăng nhập với tài khoản và mật khẩu
     *
     * @param email    Email đăng nhập
     * @param password Mật khẩu
     * @return LiveData<Resource < Auth>> kết quả đăng nhập
     */
    public LiveData<Resource<Auth>> login(String email, String password) {
        MutableLiveData<Resource<Auth>> loginResult = new MutableLiveData<>();
        loginResult.setValue(Resource.loading(null));

        LoginRequest loginRequest = new LoginRequest(email, password);
        authService.login(loginRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Auth>> call,
                                   @NonNull Response<ApiResponse<Auth>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Auth> apiResponse = response.body();

                    Auth auth = apiResponse.getData();
                    // Lưu thông tin đăng nhập vào SharedPreferences
                    if (apiResponse.isSuccess()) {
                        prefsManager.saveUser(auth.getUser());
                    }
                    prefsManager.saveAuthToken(auth.getAccessToken());
                    prefsManager.saveRefreshToken(auth.getRefreshToken());

                    loginResult.setValue(Resource.success(auth, "Login successful"));
                } else {
                    loginResult.setValue(Resource.error(ERROR_LOGIN, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Auth>> call, @NonNull Throwable t) {
                loginResult.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return loginResult;
    }

    /**
     * Đăng ký tài khoản mới
     *
     * @param username        Tên đăng nhập
     * @param email           Email đăng ký
     * @param password        Mật khẩu
     * @param confirmPassword Xác nhận mật khẩu
     * @param fullName        Họ tên đầy đủ
     * @return LiveData<Resource < Auth>> kết quả đăng ký
     */
    public LiveData<Resource<Auth>> register(String username, String email, String password, String confirmPassword, String fullName) {
        MutableLiveData<Resource<Auth>> registerResult = new MutableLiveData<>();
        registerResult.setValue(Resource.loading(null));

        RegisterRequest registerRequest = new RegisterRequest(username, email, password, confirmPassword, fullName);
        authService.register(registerRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Auth>> call,
                                   @NonNull Response<ApiResponse<Auth>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Auth> apiResponse = response.body();
                    // Lưu thông tin đăng nhập vào SharedPreferences
                    Auth auth = apiResponse.getData();
                    if (apiResponse.isSuccess()) {
                        prefsManager.saveUser(auth.getUser());
                    }
                    prefsManager.saveAuthToken(auth.getAccessToken());
                    prefsManager.saveRefreshToken(auth.getRefreshToken());

                    registerResult.setValue(Resource.success(auth, "Registration successful"));
                } else {
                    registerResult.setValue(Resource.error(ERROR_REGISTER, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Auth>> call, @NonNull Throwable t) {
                registerResult.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return registerResult;
    }

    /**
     * Đăng xuất tài khoản hiện tại
     *
     * @return LiveData<Resource < Void>> kết quả đăng xuất
     */
    public LiveData<Resource<Void>> logout() {
        MutableLiveData<Resource<Void>> logoutResult = new MutableLiveData<>();
        logoutResult.setValue(Resource.loading(null));

        String refreshToken = prefsManager.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            // Không có token, coi như đã đăng xuất
            clearUserData();
            logoutResult.setValue(Resource.success(null, "Logged out"));
            return logoutResult;
        }

        TokenRequest tokenRequest = new TokenRequest(refreshToken);
        authService.logout(tokenRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                // Xóa thông tin người dùng khỏi bộ nhớ cục bộ
                clearUserData();
                if (response.isSuccessful()) {
                    logoutResult.setValue(Resource.success(null, "Logged out"));
                } else {
                    // Vẫn xóa dữ liệu cục bộ ngay cả khi API thất bại
                    logoutResult.setValue(Resource.success(null, "Logged out from device"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Mặc dù API thất bại, vẫn xóa dữ liệu cục bộ
                clearUserData();
                logoutResult.setValue(Resource.success(null, "Logged out from device"));
            }
        });

        return logoutResult;
    }

    /**
     * Kiểm tra người dùng hiện tại đã đăng nhập hay chưa
     *
     * @return true nếu đã đăng nhập, ngược lại là false
     */
    public boolean isLoggedIn() {
        return prefsManager.getUser() != null;
    }

    /**
     * Lấy thông tin người dùng hiện tại
     *
     * @return User thông tin người dùng đã lưu trong SharedPreferences
     */
    public User getCurrentUser() {
        return prefsManager.getUser();
    }

    /**
     * Xóa dữ liệu người dùng khỏi bộ nhớ cục bộ
     */
    private void clearUserData() {
        prefsManager.clearAuthToken();
        prefsManager.clearRefreshToken();
        prefsManager.clearUser();
    }
}
