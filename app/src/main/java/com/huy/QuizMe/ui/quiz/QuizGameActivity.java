package com.huy.QuizMe.ui.quiz;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Room;

public class QuizGameActivity extends AppCompatActivity {

    private QuizGameViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(QuizGameViewModel.class);
        
        // Lấy dữ liệu từ intent
        Room room = (Room) getIntent().getSerializableExtra("ROOM");
        if (room != null) {

        } else {
            Toast.makeText(this, "Không tìm thấy thông tin phòng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Khởi tạo giao diện
        initializeViews();
        setupObservers();
    }
    
    private void initializeViews() {
        // TODO: Initialize UI components
    }
    
    private void setupObservers() {

    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký các sự kiện WebSocket
        if (viewModel != null) {

        }
    }
}