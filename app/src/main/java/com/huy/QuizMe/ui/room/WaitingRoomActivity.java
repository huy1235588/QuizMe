package com.huy.QuizMe.ui.room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.ui.quiz.QuizGameActivity;
import com.huy.QuizMe.ui.room.adapter.ChatMessageAdapter;
import com.huy.QuizMe.ui.room.adapter.ParticipantAdapter;
import com.huy.QuizMe.utils.KeyboardUtils;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WaitingRoomActivity extends AppCompatActivity {
    private WaitingRoomViewModel viewModel;
    private DrawerLayout drawerLayout;
    private RecyclerView rvChatMessages, rvParticipants;
    private ChatMessageAdapter chatAdapter;
    private ParticipantAdapter participantAdapter;
    private ImageButton btnBack, btnUsers, btnSettings, btnEmoji;
    private FloatingActionButton btnSend;
    private EditText etChatMessage;
    private TextView tvRoomDescription, tvParticipantsCount;
    private Button btnStartQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Thiết lập chế độ điều chỉnh bàn phím
        KeyboardUtils.setupKeyboardAdjustResize(this);

        setContentView(R.layout.activity_waiting_room);

        // Thiết lập chế độ điều chỉnh cửa sổ
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(WaitingRoomViewModel.class);

        // Khởi tạo giao diện
        initializeViews();
        setupBackPressHandler();
        setupRecyclerViews();
        setupListeners();

        // Lấy dữ liệu phòng từ intent
        Room room = (Room) getIntent().getSerializableExtra("ROOM");
        if (room != null) {
            viewModel.setupWaitingRoom(room);
        } else {
            Toast.makeText(this, "Error: Room information not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Quan sát các đối tượng LiveData
        observeViewModel();

        // Thiết lập DrawerLayout
        View rootView = findViewById(R.id.main);
        KeyboardUtils.setupChatAreaKeyboardListener(rootView, rvChatMessages);
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        rvChatMessages = findViewById(R.id.rvChatMessages);
        rvParticipants = findViewById(R.id.rvParticipants);
        btnBack = findViewById(R.id.btnBack);
        btnUsers = findViewById(R.id.btnUsers);
        btnSettings = findViewById(R.id.btnSettings);
        btnEmoji = findViewById(R.id.btnEmoji);
        btnSend = findViewById(R.id.btnSend);
        etChatMessage = findViewById(R.id.etChatMessage);
        tvRoomDescription = findViewById(R.id.tvRoomDescription);
        tvParticipantsCount = findViewById(R.id.tvParticipantsCount);

        // Thêm nút bắt đầu nếu cần
        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        if (btnStartQuiz != null) {
            btnStartQuiz.setVisibility(View.GONE); // Ẩn ban đầu
        }
    }

    private void setupRecyclerViews() {
        // Thiết lập RecyclerView cho tin nhắn trò chuyện
        chatAdapter = new ChatMessageAdapter(this);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        rvChatMessages.setAdapter(chatAdapter);

        // Thiết lập RecyclerView cho người tham gia
        participantAdapter = new ParticipantAdapter(this);
        rvParticipants.setLayoutManager(new LinearLayoutManager(this));
        rvParticipants.setAdapter(participantAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        btnUsers.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        btnSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Room settings feature coming soon", Toast.LENGTH_SHORT).show();
        });

        btnEmoji.setOnClickListener(v -> {
            Toast.makeText(this, "Emoji picker feature coming soon", Toast.LENGTH_SHORT).show();
        });

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String message = etChatMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            boolean sent = viewModel.sendChatMessage(message);
            if (sent) {
                etChatMessage.setText("");
            } else {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void observeViewModel() {
        // Theo dõi phòng hiện tại
        viewModel.getCurrentRoom().observe(this, room -> {
            if (room != null) {
                updateRoomInfo(room);
            }
        });

        // Theo dõi tin nhắn trò chuyện
        viewModel.getChatMessages().observe(this, messages -> {
            if (messages != null) {
                chatAdapter.setMessages(messages);
                // Cuộn tới tin nhắn mới nhất
                if (!messages.isEmpty()) {
                    rvChatMessages.smoothScrollToPosition(messages.size() - 1);
                }
            }
        });

        // Theo dõi danh sách người tham gia
        viewModel.getParticipants().observe(this, participants -> {
            if (participants != null) {
                participantAdapter.setParticipants(participants);
                updateParticipantsCount(participants.size());
            }
        });

        // Theo dõi trạng thái kết nối WebSocket
        viewModel.getIsConnected().observe(this, isConnected -> {
            // Hiển thị/ẩn chỉ báo lỗi mạng nếu cần
        });

        // Theo dõi thông báo lỗi
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Theo dõi sự kiện bắt đầu trò chơi
        viewModel.getGameStartEvent().observe(this, roomResource -> {
            if (roomResource != null && roomResource.getStatus() == Resource.Status.SUCCESS) {
                // Xử lý bắt đầu trò chơi
                Toast.makeText(this, "Game starting...", Toast.LENGTH_SHORT).show();

                // Chuyển hướng đến màn hình trò chơi
                Intent intent = new Intent(this, QuizGameActivity.class);
                intent.putExtra("ROOM", roomResource.getData());
                startActivity(intent);
                finish();
            }
        });

        // Theo dõi sự kiện đóng phòng
        viewModel.getGameCloseEvent().observe(this, roomResource -> {
            if (roomResource != null && roomResource.getStatus() == Resource.Status.SUCCESS) {
                Toast.makeText(this, "Room has been closed", Toast.LENGTH_SHORT).show();

                // Quay lại
                finish();
            }
        });

        // Kiểm tra xem người dùng hiện tại có phải là chủ phòng để hiển thị nút bắt đầu
        if (viewModel.isCurrentUserHost()) {
            if (btnStartQuiz != null) {
                btnStartQuiz.setVisibility(View.VISIBLE);
                btnStartQuiz.setOnClickListener(v -> startGame());
            }
        }
    }

    private void updateRoomInfo(Room room) {
        if (room.getHost() != null) {
            String description = "Waiting for " + room.getHost().getUsername() + " to start the quiz...";
            tvRoomDescription.setText(description);
        }
    }

    private void updateParticipantsCount(int count) {
        String countText = count + " " + getString(R.string.participants);
        tvParticipantsCount.setText(countText);
    }

    private void startGame() {
        viewModel.startGame().observe(this, result -> {
            if (result != null) {
                if (result.getStatus() == Resource.Status.SUCCESS) {
                    Toast.makeText(this, "Game started!", Toast.LENGTH_SHORT).show();

                    // Lấy thông tin phòng mới cập nhật
                    Room room = result.getData();
                    if (room != null) {
                        // Chuyển sang màn hình chơi quiz với thông tin phòng
                        Intent intent = new Intent(this, QuizGameActivity.class);
                        intent.putExtra("ROOM", room);
                        startActivity(intent);
                        finish(); // Đóng màn hình chờ
                    }

                } else if (result.getStatus() == Resource.Status.ERROR) {
                    Toast.makeText(this, "Error starting game: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else if (viewModel.isCurrentUserHost()) {
                    // Hiển thị hộp thoại xác nhận đóng phòng nếu người dùng là chủ phòng
                    showCloseRoomConfirmationDialog();
                } else {
                    // Người chơi bình thường thì rời phòng
                    leaveRoom();
                }
            }
        });
    }

    private void showCloseRoomConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Đóng phòng");
        builder.setMessage("Bạn có chắc muốn đóng phòng không? Tất cả người chơi sẽ bị đẩy ra khỏi phòng.");
        builder.setPositiveButton("Đồng ý", (dialog, which) -> {
            closeRoom();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setCancelable(true);
        builder.show();
    }

    private void closeRoom() {
        viewModel.closeRoom().observe(this, result -> {
            if (result != null) {
                if (result.getStatus() == Resource.Status.SUCCESS) {
                    Toast.makeText(this, "Phòng đã được đóng", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (result.getStatus() == Resource.Status.ERROR) {
                    Toast.makeText(this, "Lỗi khi đóng phòng: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void leaveRoom() {
        viewModel.leaveRoom().observe(this, result -> {
            if (result != null) {
                if (result.getStatus() == Resource.Status.SUCCESS) {
                    Toast.makeText(this, "Left room", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (result.getStatus() == Resource.Status.ERROR) {
                    Toast.makeText(this, "Error leaving room: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đảm bảo hủy đăng ký khỏi các sự kiện WebSocket
        if (viewModel != null) {
            viewModel.unsubscribeFromEvents();
        }
    }
}