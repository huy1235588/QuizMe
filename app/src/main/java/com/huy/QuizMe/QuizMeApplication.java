package com.huy.QuizMe;

import android.app.Application;
import android.content.Context;

import com.huy.QuizMe.utils.SharedPreferencesManager;
import com.huy.QuizMe.utils.LanguageUtils;

public class QuizMeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo SharedPreferencesManager để quản lý dữ liệu người dùng
        SharedPreferencesManager.init(this);

        // Áp dụng ngôn ngữ đã được lưu
        new LanguageUtils(SharedPreferencesManager.getInstance());
    }
}