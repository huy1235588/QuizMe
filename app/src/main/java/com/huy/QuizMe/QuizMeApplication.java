package com.huy.QuizMe;

import android.app.Application;
import com.huy.QuizMe.utils.SharedPreferencesManager;

public class QuizMeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo SharedPreferencesManager để quản lý dữ liệu người dùng
        SharedPreferencesManager.init(this);
    }
}