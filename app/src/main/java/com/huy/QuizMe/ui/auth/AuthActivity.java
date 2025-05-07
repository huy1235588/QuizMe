package com.huy.QuizMe.ui.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.huy.QuizMe.R;
import com.huy.QuizMe.databinding.ActivityAuthBinding;

/**
 * AuthActivity - Màn hình container cho các fragment xác thực (đăng nhập, đăng ký)
 */
public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hiển thị fragment đăng nhập mặc định khi khởi động
        if (savedInstanceState == null) {
            showFragment(new LoginFragment());
        }
    }

    /**
     * Hiển thị fragment được chỉ định
     * 
     * @param fragment Fragment cần hiển thị
     */
    public void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.auth_container, fragment)
                .commit();
    }

    /**
     * Hiển thị fragment được chỉ định và thêm vào back stack
     * 
     * @param fragment Fragment cần hiển thị
     */
    public void showFragmentWithBackStack(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.auth_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}