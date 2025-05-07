package com.huy.QuizMe.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huy.QuizMe.data.model.Auth;
import com.huy.QuizMe.data.repository.AuthRepository;
import com.huy.QuizMe.data.repository.Resource;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Resource<Auth>> loginResult = new MutableLiveData<>();

    public LoginViewModel() {
        this.authRepository = new AuthRepository();
    }

    /**
     * Thực hiện đăng nhập với email và mật khẩu
     *
     * @param email    email đăng nhập
     * @param password mật khẩu
     */
    public void login(String email, String password) {
        // Sử dụng repository để thực hiện đăng nhập
        authRepository.login(email, password).observeForever(result -> {
            loginResult.setValue(result);
        });
    }

    /**
     * Kiểm tra người dùng đã đăng nhập hay chưa
     *
     * @return true nếu người dùng đã đăng nhập, false nếu chưa
     */
    public boolean isUserLoggedIn() {
        return authRepository.isLoggedIn();
    }

    /**
     * Lấy kết quả đăng nhập
     *
     * @return LiveData<Resource<Auth>> kết quả đăng nhập
     */
    public LiveData<Resource<Auth>> getLoginResult() {
        return loginResult;
    }
}