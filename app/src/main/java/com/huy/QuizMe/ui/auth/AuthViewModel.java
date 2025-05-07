package com.huy.QuizMe.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huy.QuizMe.data.model.Auth;
import com.huy.QuizMe.data.repository.AuthRepository;
import com.huy.QuizMe.data.repository.Resource;

public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Resource<Auth>> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<Auth>> registerResult = new MutableLiveData<>();

    public AuthViewModel() {
        this.authRepository = new AuthRepository();
    }

    /**
     * Thực hiện đăng nhập với email và mật khẩu
     *
     * @param email    email đăng nhập
     * @param password mật khẩu
     */
    public void login(String email, String password) {
        authRepository.login(email, password).observeForever(result -> {
            loginResult.setValue(result);
        });
    }

    /**
     * Thực hiện đăng ký tài khoản mới
     *
     * @param username        tên đăng nhập
     * @param email           địa chỉ email
     * @param password        mật khẩu
     * @param confirmPassword xác nhận mật khẩu
     * @param fullName        họ tên đầy đủ
     */
    public void register(String username, String email, String password, String confirmPassword, String fullName) {
        authRepository.register(username, email, password, confirmPassword, fullName).observeForever(result -> {
            registerResult.setValue(result);
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

    /**
     * Lấy kết quả đăng ký
     *
     * @return LiveData<Resource<Auth>> kết quả đăng ký
     */
    public LiveData<Resource<Auth>> getRegisterResult() {
        return registerResult;
    }
}