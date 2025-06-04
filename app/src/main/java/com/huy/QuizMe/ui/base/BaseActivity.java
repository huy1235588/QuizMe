package com.huy.QuizMe.ui.base;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.huy.QuizMe.utils.LanguageUtils;

/**
 * BaseActivity - Activity cơ sở cho tất cả các Activity khác
 * Tự động áp dụng ngôn ngữ đã được chọn
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Áp dụng ngôn ngữ đã được lưu
        LanguageUtils.setAppLanguage(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        // Áp dụng ngôn ngữ khi attach context
        super.attachBaseContext(LanguageUtils.setAppLanguage(base));
    }

    /**
     * Phương thức để restart activity sau khi thay đổi ngôn ngữ
     */
    protected void restartActivity() {
        recreate();
    }
}
