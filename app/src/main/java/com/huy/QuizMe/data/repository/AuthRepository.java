package com.huy.QuizMe.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huy.QuizMe.data.api.ApiClient;
import com.huy.QuizMe.data.api.AuthService;
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
    private static final String ERROR_LOGIN = "Đăng nhập thất bại";
    private static final String ERROR_REGISTER = "Đăng ký thất bại";
    private static final String ERROR_LOGOUT = "Đăng xuất thất bại";

    public AuthRepository() {
        this.authService = ApiClient.getInstance().getAuthService();
        this.prefsManager = SharedPreferencesManager.getInstance();
    }

    /**
     * Đăng nhập với tài khoản và mật khẩu
     *
     * @param email    Email đăng nhập
     * @param password Mật khẩu
     * @return LiveData<Resource<Auth>> kết quả đăng nhập
     */
    public LiveData<Resource<Auth>> login(String email, String password) {
        MutableLiveData<Resource<Auth>> loginResult = new MutableLiveData<>();
        loginResult.setValue(Resource.loading(null));

        LoginRequest loginRequest = new LoginRequest(email, password);
        authService.login(loginRequest).enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(@NonNull Call<Auth> call,
                                   @NonNull Response<Auth> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Auth auth = response.body();

                    // Lưu thông tin đăng nhập vào SharedPreferences
                    if (auth.getUser() != null) {
                        prefsManager.saveUser(auth.getUser());
                    }
                    prefsManager.saveAuthToken(auth.getAccessToken());
                    prefsManager.saveRefreshToken(auth.getRefreshToken());

                    loginResult.setValue(Resource.success(auth, "Đăng nhập thành công"));
                } else {
                    loginResult.setValue(Resource.error(ERROR_LOGIN, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Auth> call, @NonNull Throwable t) {
                loginResult.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return loginResult;
    }

    /**
     * Đăng ký tài khoản mới
     *
     * @param name        Tên người dùng
     * @param email       Email đăng ký
     * @param password    Mật khẩu
     * @param phoneNumber Số điện thoại
     * @param avatar      Ảnh đại diện
     * @return LiveData<Resource < Auth>> kết quả đăng ký
     */
    public LiveData<Resource<Auth>> register(String name, String email, String password, String phoneNumber, String avatar) {
        MutableLiveData<Resource<Auth>> registerResult = new MutableLiveData<>();
        registerResult.setValue(Resource.loading(null));

        RegisterRequest registerRequest = new RegisterRequest(name, email, password, phoneNumber, avatar);
        authService.register(registerRequest).enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(@NonNull Call<Auth> call,
                                   @NonNull Response<Auth> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Auth auth = response.body();
                    // Lưu thông tin đăng nhập vào SharedPreferences
                    if (auth.getUser() != null) {
                        prefsManager.saveUser(auth.getUser());
                    }
                    prefsManager.saveAuthToken(auth.getAccessToken());
                    prefsManager.saveRefreshToken(auth.getRefreshToken());

                    registerResult.setValue(Resource.success(auth, "Đăng ký thành công"));
                } else {
                    registerResult.setValue(Resource.error(ERROR_REGISTER, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Auth> call, @NonNull Throwable t) {
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
            logoutResult.setValue(Resource.success(null, "Đã đăng xuất"));
            return logoutResult;
        }

        TokenRequest tokenRequest = new TokenRequest(refreshToken);
        authService.logout(tokenRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                // Xóa thông tin người dùng khỏi bộ nhớ cục bộ
                clearUserData();
                if (response.isSuccessful()) {
                    logoutResult.setValue(Resource.success(null, "Đã đăng xuất"));
                } else {
                    // Vẫn xóa dữ liệu cục bộ ngay cả khi API thất bại
                    logoutResult.setValue(Resource.success(null, "Đã đăng xuất khỏi thiết bị"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Mặc dù API thất bại, vẫn xóa dữ liệu cục bộ
                clearUserData();
                logoutResult.setValue(Resource.success(null, "Đã đăng xuất khỏi thiết bị"));
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
