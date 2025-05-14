package com.huy.QuizMe.ui.main.join;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.PagedResponse;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.model.request.RoomRequest;
import com.huy.QuizMe.ui.room.WaitingRoomActivity;
import com.huy.QuizMe.utils.ApiUtils;

public class CreateRoomFragment extends Fragment {
    private CreateRoomViewModel viewModel;

    // Các thành phần UI
    private TextInputLayout tilRoomName;
    private TextInputEditText etRoomName;
    private TextInputLayout tilRoomDescription;
    private TextInputEditText etRoomDescription;
    private TextInputLayout tilMaxParticipants;
    private TextInputEditText etMaxParticipants;
    private TextInputLayout tilCategory;
    private AutoCompleteTextView actCategory;
    private SwitchMaterial switchPublic;
    private SwitchMaterial switchPassword;
    private TextInputLayout tilPassword;
    private TextInputEditText etPassword;
    private RecyclerView rvQuizzes;
    private Button btnCreateRoom;
    private ProgressBar progressBar;

    // Adapter
    private QuizAdapter quizAdapter;

    private static final int MAX_PARTICIPANTS_LIMIT = 100;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CreateRoomViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_room, container, false);

        // Khởi tạo các thành phần UI
        initializeViews(view);
        setupListeners();
        loadQuizzes();

        return view;
    }

    private void initializeViews(View view) {
        tilRoomName = view.findViewById(R.id.til_room_name);
        etRoomName = view.findViewById(R.id.et_room_name);
        tilRoomDescription = view.findViewById(R.id.til_room_description);
        etRoomDescription = view.findViewById(R.id.et_room_description);
        tilMaxParticipants = view.findViewById(R.id.til_max_participants);
        etMaxParticipants = view.findViewById(R.id.et_max_participants);
        switchPublic = view.findViewById(R.id.switch_public);
        switchPassword = view.findViewById(R.id.switch_password);
        tilPassword = view.findViewById(R.id.til_password);
        etPassword = view.findViewById(R.id.et_password);
        rvQuizzes = view.findViewById(R.id.rv_quizzes);
        btnCreateRoom = view.findViewById(R.id.btn_create_room);
        progressBar = view.findViewById(R.id.progress_bar);

        // Thiết lập RecyclerView
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        quizAdapter = new QuizAdapter(requireContext());
        rvQuizzes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQuizzes.setAdapter(quizAdapter);
        rvQuizzes.setHasFixedSize(true);
    }

    private void setupListeners() {
        // Chuyển đổi hiển thị trường mật khẩu dựa trên trạng thái công tắc
        switchPassword.setOnCheckedChangeListener((buttonView, isChecked) -> 
            tilPassword.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        // Thiết lập người lắng nghe sự kiện chọn quiz
        quizAdapter.setOnQuizClickListener(quiz -> {
            // Xử lý khi chọn quiz
            viewModel.setSelectedQuiz(quiz);
            quizAdapter.notifyDataSetChanged();
        });

        // Thiết lập người lắng nghe sự kiện nút tạo phòng
        btnCreateRoom.setOnClickListener(v -> {
            if (validateInputs()) {
                createRoom();
            }
        });
    }

    private void loadQuizzes() {
        // Thiết lập người quan sát cho các quiz
        viewModel.loadTrendingQuizzes(0, DEFAULT_PAGE_SIZE, null, null, null, true)
                .observe(getViewLifecycleOwner(), resource -> {
                    toggleLoading(ApiUtils.isLoading(resource));
                    
                    if (ApiUtils.isSuccess(resource)) {
                        PagedResponse<Quiz> quizzes = resource.getData();
                        if (quizzes != null && quizzes.getContent() != null) {
                            quizAdapter.updateItems(quizzes);
                        } else {
                            showToast("No quizzes available");
                        }
                    } else if (!ApiUtils.isLoading(resource)) {
                        showToast(resource.getMessage());
                    }
                });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Xác thực tên phòng
        String roomName = getTextFromEditText(etRoomName);
        if (TextUtils.isEmpty(roomName)) {
            tilRoomName.setError("Room name cannot be empty");
            isValid = false;
        } else {
            tilRoomName.setError(null);
        }

        // Xác thực số người tham gia tối đa
        String maxParticipantsStr = getTextFromEditText(etMaxParticipants);
        if (TextUtils.isEmpty(maxParticipantsStr)) {
            tilMaxParticipants.setError("Maximum participants cannot be empty");
            isValid = false;
        } else {
            try {
                int maxParticipants = Integer.parseInt(maxParticipantsStr);
                if (maxParticipants <= 0 || maxParticipants > MAX_PARTICIPANTS_LIMIT) {
                    tilMaxParticipants.setError("Please enter a number between 1 and " + MAX_PARTICIPANTS_LIMIT);
                    isValid = false;
                } else {
                    tilMaxParticipants.setError(null);
                }
            } catch (NumberFormatException e) {
                tilMaxParticipants.setError("Please enter a valid number");
                isValid = false;
            }
        }

        // Xác thực mật khẩu nếu được yêu cầu
        if (switchPassword.isChecked() && TextUtils.isEmpty(getTextFromEditText(etPassword))) {
            tilPassword.setError("Password cannot be empty");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        // Xác thực lựa chọn quiz
        if (viewModel.getSelectedQuiz() == null) {
            showToast("Please select a quiz");
            isValid = false;
        }

        return isValid;
    }

    private void createRoom() {
        toggleLoading(true);

        // Xây dựng đối tượng room request
        RoomRequest roomRequest = buildRoomRequest();

        // Tạo phòng
        viewModel.createRoom(roomRequest)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (ApiUtils.isLoading(resource)) {
                        toggleLoading(true);
                    } else if (ApiUtils.isSuccess(resource)) {
                        toggleLoading(false);
                        showToast("Room created successfully");

                        // Điều hướng đến activity phòng chờ
                        Room room = resource.getData();
                        if (room != null) {
                            // Chuyển đến activity phòng chờ
                            Intent intent = new Intent(getContext(), WaitingRoomActivity.class);
                            intent.putExtra("ROOM", room);
                            startActivity(intent);
                        }                        

                    } else {
                        toggleLoading(false);
                        showToast(resource.getMessage() != null ? 
                            resource.getMessage() : "Failed to create room");
                    }
                });
    }

    private RoomRequest buildRoomRequest() {
        Room room = new Room();
        room.setName(getTextFromEditText(etRoomName));
        room.setMaxPlayers(Integer.parseInt(getTextFromEditText(etMaxParticipants)));
        room.setPublic(switchPublic.isChecked());
        room.setHasPassword(switchPassword.isChecked());
        room.setQuiz(viewModel.getSelectedQuiz());

        String description = getTextFromEditText(etRoomDescription);
        if (!TextUtils.isEmpty(description)) {
            // Đặt mô tả nếu được cung cấp (giả sử có phương thức setDescription)
            // room.setDescription(description);
        }

        return new RoomRequest(
                room.getName(),
                room.getQuiz().getId(),
                room.getMaxPlayers(),
                switchPassword.isChecked() ? getTextFromEditText(etPassword) : null,
                room.isPublic()
        );
    }

    private String getTextFromEditText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void toggleLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnCreateRoom.setEnabled(!isLoading);
    }

    private void showToast(String message) {
        if (getContext() != null && message != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}