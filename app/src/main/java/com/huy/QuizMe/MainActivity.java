package com.huy.QuizMe;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.huy.QuizMe.databinding.ActivityMainBinding;
import com.huy.QuizMe.ui.home.HomeFragment;
import com.huy.QuizMe.ui.library.LibraryFragment;
import com.huy.QuizMe.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Fragment HomeFragment
        HomeFragment homeFragment = new HomeFragment();
        // Thay thế Fragment mặc định
        replaceFragment(homeFragment);

        // Thiết lập sự kiện cho các nút điều hướng
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Thay thế Fragment dựa trên nút được chọn
            if (itemId == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.nav_library) {
                replaceFragment(new LibraryFragment());
            } else if (itemId == R.id.nav_profile) {
                replaceFragment(new ProfileFragment());
            } else {
                return false;
            }

            return true;
        });
    }

    /**
     * Thay thế Fragment hiện tại bằng Fragment mới
     * @param fragment Fragment mới để hiển thị
     */
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frm_container, fragment)
                .commit();
    }
}