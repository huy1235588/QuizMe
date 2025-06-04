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
        LanguageUtils.setAppLanguage(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        // Khởi tạo SharedPreferencesManager trước khi áp dụng ngôn ngữ
        SharedPreferencesManager.init(base);

        // Áp dụng ngôn ngữ đã được lưu
        super.attachBaseContext(LanguageUtils.setAppLanguage(base));
    }
}