package com.huy.QuizMe.ui.main.join;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.data.model.request.RoomRequest;
import com.huy.QuizMe.utils.ApiUtils;

import java.util.ArrayList;
import java.util.List;

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

    // Các adapter
    private QuizAdapter quizAdapter;

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
        quizAdapter = new QuizAdapter(requireContext());
        rvQuizzes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvQuizzes.setAdapter(quizAdapter);
    }

    private void setupListeners() {
        // Chuyển đổi hiển thị trường mật khẩu dựa trên trạng thái công tắc
        switchPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tilPassword.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Thiết lập người lắng nghe sự kiện chọn quiz
        quizAdapter.setOnQuizClickListener(quiz -> {
            // Xử lý khi chọn quiz
            viewModel.setSelectedQuiz(quiz);
            quizAdapter.notifyDataSetChanged(); // Cập nhật UI để phản ánh lựa chọn
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
        viewModel.loadTrendingQuizzes(0, 10, null, null, null, true)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (ApiUtils.isLoading(resource)) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else if (ApiUtils.isSuccess(resource)) {
                        progressBar.setVisibility(View.GONE);
                        PagedResponse<Quiz> quizzes = resource.getData();
                        if (quizzes != null && quizzes.getContent() != null) {
                            quizAdapter.updateItems(quizzes);
                        } else {
                            Toast.makeText(getContext(), "No quizzes available", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Xác thực tên phòng
        if (etRoomName.getText().toString().trim().isEmpty()) {
            tilRoomName.setError("Room name cannot be empty");
            isValid = false;
        } else {
            tilRoomName.setError(null);
        }

        // Xác thực số người tham gia tối đa
        if (etMaxParticipants.getText().toString().trim().isEmpty()) {
            tilMaxParticipants.setError("Maximum participants cannot be empty");
            isValid = false;
        } else {
            try {
                int maxParticipants = Integer.parseInt(etMaxParticipants.getText().toString().trim());
                if (maxParticipants <= 0 || maxParticipants > 100) { // Giới hạn trên tùy ý
                    tilMaxParticipants.setError("Please enter a number between 1 and 100");
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
        if (switchPassword.isChecked() && etPassword.getText().toString().trim().isEmpty()) {
            tilPassword.setError("Password cannot be empty");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        // Xác thực lựa chọn quiz
        if (viewModel.getSelectedQuiz() == null) {
            Toast.makeText(getContext(), "Please select a quiz", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void createRoom() {
        // Hiển thị tiến trình
        progressBar.setVisibility(View.VISIBLE);
        btnCreateRoom.setEnabled(false);

        // Xây dựng đối tượng phòng
        Room room = new Room();
        room.setName(etRoomName.getText().toString().trim());
        room.setMaxPlayers(Integer.parseInt(etMaxParticipants.getText().toString().trim()));
        room.setPublic(switchPublic.isChecked());
        room.setHasPassword(switchPassword.isChecked());

        if (etRoomDescription.getText() != null && !etRoomDescription.getText().toString().trim().isEmpty()) {
            // Đặt mô tả nếu được cung cấp (giả sử có phương thức setDescription)
            // room.setDescription(etRoomDescription.getText().toString().trim());
        }

        // Đặt quiz
        room.setQuiz(viewModel.getSelectedQuiz());

        RoomRequest roomRequest = new RoomRequest(
                room.getName(),
                room.getQuiz().getId(),
                room.getMaxPlayers(),
                switchPassword.isChecked() ? etPassword.getText().toString() : null,
                room.isPublic()
        );

        // Tạo phòng
        viewModel.createRoom(roomRequest)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (ApiUtils.isLoading(resource)) {
                        progressBar.setVisibility(View.VISIBLE);
                        btnCreateRoom.setEnabled(false);
                    } else if (ApiUtils.isSuccess(resource)) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to create room", Toast.LENGTH_SHORT).show();

                        // Điều hướng trở lại màn hình tham gia phòng
                        getParentFragmentManager().popBackStack();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnCreateRoom.setEnabled(true);
                        Toast.makeText(getContext(), resource.getMessage() != null ?
                                        resource.getMessage() : "Room created successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}