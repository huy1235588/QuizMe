package com.huy.QuizMe.ui.room;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.huy.QuizMe.ui.room.adapter.ChatMessageAdapter;
import com.huy.QuizMe.ui.room.adapter.ParticipantAdapter;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WaitingRoomActivity extends AppCompatActivity {
    private WaitingRoomViewModel viewModel;
    private DrawerLayout drawerLayout;
    private RecyclerView rvChatMessages;
    private RecyclerView rvParticipants;
    private ChatMessageAdapter chatAdapter;
    private ParticipantAdapter participantAdapter;
    private ImageButton btnBack;
    private ImageButton btnUsers;
    private ImageButton btnSettings;
    private ImageButton btnEmoji;
    private FloatingActionButton btnSend;
    private EditText etChatMessage;
    private TextView tvRoomDescription;
    private TextView tvParticipantsCount;
    private Button btnStartQuiz;
    private LinearLayout networkErrorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_waiting_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(WaitingRoomViewModel.class);
        
        // Initialize views
        initializeViews();
        setupRecyclerViews();
        setupListeners();
        
        // Get Room data from intent
        Room room = (Room) getIntent().getSerializableExtra("ROOM");
        if (room != null) {
            viewModel.setupWaitingRoom(room);
        } else {
            Toast.makeText(this, "Error: Room information not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Observe LiveData objects
        observeViewModel();
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
        
        // Add start quiz button if needed
        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        if (btnStartQuiz != null) {
            btnStartQuiz.setVisibility(View.GONE); // Initially hide it
        }
    }
    
    private void setupRecyclerViews() {
        // Setup chat messages RecyclerView
        chatAdapter = new ChatMessageAdapter(this);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        rvChatMessages.setAdapter(chatAdapter);
        
        // Setup participants RecyclerView
        participantAdapter = new ParticipantAdapter(this);
        rvParticipants.setLayoutManager(new LinearLayoutManager(this));
        rvParticipants.setAdapter(participantAdapter);
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        
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
        // Observe current room
        viewModel.getCurrentRoom().observe(this, room -> {
            if (room != null) {
                updateRoomInfo(room);
            }
        });
        
        // Observe chat messages
        viewModel.getChatMessages().observe(this, messages -> {
            if (messages != null) {
                chatAdapter.setMessages(messages);
                // Scroll to the latest message
                if (messages.size() > 0) {
                    rvChatMessages.smoothScrollToPosition(messages.size() - 1);
                }
            }
        });
        
        // Observe participants list
        viewModel.getParticipants().observe(this, participants -> {
            if (participants != null) {
                participantAdapter.setParticipants(participants);
                updateParticipantsCount(participants.size());
            }
        });
        
        // Observe WebSocket connection status
        viewModel.getIsConnected().observe(this, isConnected -> {
            // Show/hide network error indicator if needed
        });
        
        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Observe game start event
        viewModel.getGameStartEvent().observe(this, roomResource -> {
            if (roomResource != null && roomResource.getStatus() == Resource.Status.SUCCESS) {
                // Handle game start
                Toast.makeText(this, "Game starting...", Toast.LENGTH_SHORT).show();
                // Navigate to game activity
            }
        });
        
        // Check if current user is host to show start button
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
                    // Navigation will be handled by the observer for game start event
                } else if (result.getStatus() == Resource.Status.ERROR) {
                    Toast.makeText(this, "Error starting game: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            leaveRoom();
        }
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
        // Make sure to unsubscribe from WebSocket events
        if (viewModel != null) {
            viewModel.unsubscribeFromEvents();
        }
    }
}