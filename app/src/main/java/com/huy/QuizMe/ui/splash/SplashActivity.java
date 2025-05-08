package com.huy.QuizMe.ui.splash;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.huy.QuizMe.MainActivity;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.repository.AuthRepository;
import com.huy.QuizMe.ui.auth.AuthActivity;
import com.huy.QuizMe.utils.NetworkUtils;
import com.huy.QuizMe.utils.SharedPreferencesManager;

/**
 * SplashActivity - Activity hiển thị màn hình chào mừng và kiểm tra trạng thái đăng nhập
 */
public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DELAY = 1500; // 1.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Đảm bảo SharedPreferencesManager đã được khởi tạo
        SharedPreferencesManager.init(getApplicationContext());
        
        // Hiển thị splash trong 1.5 giây, sau đó kiểm tra kết nối mạng và đăng nhập
        new Handler(Looper.getMainLooper()).postDelayed(this::checkInternetConnection, SPLASH_DELAY);
    }
    
    /**
     * Kiểm tra kết nối internet trước khi kiểm tra đăng nhập
     */
    private void checkInternetConnection() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            // Nếu có kết nối internet, kiểm tra đăng nhập
            checkLoginStatus();
        } else {
            // Nếu không có kết nối internet, hiển thị thông báo
            showNoInternetDialog();
        }
    }
    
    /**
     * Kiểm tra trạng thái đăng nhập
     */
    private void checkLoginStatus() {
        // Tạo AuthRepository để kiểm tra trạng thái đăng nhập
        AuthRepository authRepository = new AuthRepository();
        
        // Kiểm tra trạng thái đăng nhập
        if (authRepository.isLoggedIn()) {
            // Nếu đã đăng nhập, chuyển đến MainActivity
            navigateToMainActivity();
        } else {
            // Nếu chưa đăng nhập, chuyển đến AuthActivity
            navigateToAuthActivity();
        }
        
        // Đóng SplashActivity
        finish();
    }
    
    /**
     * Hiển thị dialog khi không có kết nối internet
     */
    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Không có kết nối internet")
               .setMessage("Ứng dụng cần kết nối internet để hoạt động. Vui lòng kiểm tra kết nối mạng và thử lại.")
               .setPositiveButton("Cài đặt Wi-Fi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Mở cài đặt mạng
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        finish();
                    }
                })
               .setNegativeButton("Thử lại", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thử lại việc kiểm tra kết nối
                        checkInternetConnection();
                    }
                })
               .setNeutralButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng ứng dụng
                        finish();
                    }
                })
               .setCancelable(false)
               .show();
    }
    
    /**
     * Chuyển đến màn hình chính
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    
    /**
     * Chuyển đến màn hình đăng nhập/đăng ký
     */
    private void navigateToAuthActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}