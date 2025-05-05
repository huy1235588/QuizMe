package com.huy.QuizMe;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.huy.QuizMe.databinding.ActivityMainBinding;
import com.huy.QuizMe.ui.main.home.HomeFragment;
import com.huy.QuizMe.ui.main.join.JoinRoomFragment;
import com.huy.QuizMe.ui.main.library.LibraryFragment;
import com.huy.QuizMe.ui.main.profile.ProfileFragment;

/**
 * MainActivity - màn hình chính của ứng dụng QuizMe
 * Quản lý điều hướng giữa các màn hình chính thông qua bottom navigation
 */
public class MainActivity extends AppCompatActivity {

    // ViewBinding cho activity main
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng ViewBinding để truy cập view
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hiển thị màn hình trang chủ mặc định khi khởi động ứng dụng
        replaceFragment(new HomeFragment());

        // Thiết lập xử lý sự kiện cho bottom navigation
        setupBottomNavigation();
    }

    /**
     * Thiết lập xử lý sự kiện cho bottom navigation
     */
    private void setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Chuyển đổi giữa các fragment dựa trên menu item được chọn
            if (itemId == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.nav_library) {
                replaceFragment(new LibraryFragment());
            } else if (itemId == R.id.nav_profile) {
                replaceFragment(new ProfileFragment());
            } else if (itemId == R.id.nav_join) {
                replaceFragment(new JoinRoomFragment());
            } else {
                return false;
            }

            return true;
        });
    }

    /**
     * Thay thế Fragment hiện tại trong container bằng Fragment mới
     *
     * @param fragment Fragment mới để hiển thị
     */
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frm_container, fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Giải phóng binding khi activity bị hủy để tránh memory leak
        binding = null;
    }
}