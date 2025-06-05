package com.huy.QuizMe.ui.main.join;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
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
import com.huy.QuizMe.utils.ImageLoader;

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
    private Button btnCreateRoom;
    private ProgressBar progressBar;

    // Quiz selection components
    private MaterialCardView cardQuizSelection;
    private ImageView ivSelectedQuizThumbnail;
    private TextView tvSelectedQuizTitle;
    private TextView tvSelectedQuizDescription;

    // Dialog
    private QuizSelectionDialog quizSelectionDialog;

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
        View view = inflater.inflate(R.layout.fragment_create_room, container, false);        // Khởi tạo các thành phần UI
        initializeViews(view);
        setupListeners();
        setupQuizSelectionDialog();

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
        btnCreateRoom = view.findViewById(R.id.btn_create_room);
        progressBar = view.findViewById(R.id.progress_bar);

        // Quiz selection components
        cardQuizSelection = view.findViewById(R.id.card_quiz_selection);
        ivSelectedQuizThumbnail = view.findViewById(R.id.iv_selected_quiz_thumbnail);
        tvSelectedQuizTitle = view.findViewById(R.id.tv_selected_quiz_title);
        tvSelectedQuizDescription = view.findViewById(R.id.tv_selected_quiz_description);
    }

    private void setupListeners() {
        // Chuyển đổi hiển thị trường mật khẩu dựa trên trạng thái công tắc
        switchPassword.setOnCheckedChangeListener((buttonView, isChecked) ->
                tilPassword.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        // Thiết lập người lắng nghe sự kiện chọn quiz
        cardQuizSelection.setOnClickListener(v -> {
            if (quizSelectionDialog != null) {
                quizSelectionDialog.show();
            }
        });

        // Thiết lập người lắng nghe sự kiện nút tạo phòng
        btnCreateRoom.setOnClickListener(v -> {
            if (validateInputs()) {
                createRoom();
            }
        });
    }

    private void setupQuizSelectionDialog() {
        quizSelectionDialog = new QuizSelectionDialog(
                requireContext(),
                viewModel,
                getViewLifecycleOwner(),
                this::onQuizSelected
        );
    }

    private void onQuizSelected(Quiz quiz) {
        viewModel.setSelectedQuiz(quiz);
        updateSelectedQuizDisplay(quiz);
    }

    private void updateSelectedQuizDisplay(Quiz quiz) {
        if (quiz != null) {
            tvSelectedQuizTitle.setText(quiz.getTitle());
            tvSelectedQuizTitle.setTextColor(getResources().getColor(R.color.black));

            String description = quiz.getDescription();
            if (description != null && !description.isEmpty()) {
                tvSelectedQuizDescription.setText(description);
            } else {
                tvSelectedQuizDescription.setText(getString(R.string.questions_format, quiz.getQuestionCount()));
            }
            tvSelectedQuizDescription.setTextColor(getResources().getColor(R.color.gray));

            // Load quiz thumbnail
            if (quiz.getQuizThumbnails() != null && !quiz.getQuizThumbnails().isEmpty()) {
                ImageLoader.loadImageWithTransformations(requireContext(),
                        ivSelectedQuizThumbnail,
                        quiz.getQuizThumbnails(),
                        R.drawable.placeholder_quiz,
                        R.drawable.placeholder_quiz);
            } else {
                ivSelectedQuizThumbnail.setImageResource(R.drawable.placeholder_quiz);
            }
        } else {
            // Reset to default state
            tvSelectedQuizTitle.setText(getString(R.string.tap_to_select_quiz_hint));
            tvSelectedQuizTitle.setTextColor(getResources().getColor(R.color.gray));
            tvSelectedQuizDescription.setText(getString(R.string.choose_from_available_quizzes_hint));
            tvSelectedQuizDescription.setTextColor(getResources().getColor(R.color.gray));
            ivSelectedQuizThumbnail.setImageResource(R.drawable.placeholder_quiz);
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;        // Xác thực tên phòng
        String roomName = getTextFromEditText(etRoomName);
        if (TextUtils.isEmpty(roomName)) {
            tilRoomName.setError(getString(R.string.room_name_cannot_be_empty));
            isValid = false;
        } else {
            tilRoomName.setError(null);
        }

        // Xác thực số người tham gia tối đa
        String maxParticipantsStr = getTextFromEditText(etMaxParticipants);
        if (TextUtils.isEmpty(maxParticipantsStr)) {
            tilMaxParticipants.setError(getString(R.string.max_participants_cannot_be_empty));
            isValid = false;
        } else {
            try {
                int maxParticipants = Integer.parseInt(maxParticipantsStr);
                if (maxParticipants <= 0 || maxParticipants > MAX_PARTICIPANTS_LIMIT) {
                    tilMaxParticipants.setError(getString(R.string.enter_number_between, MAX_PARTICIPANTS_LIMIT));
                    isValid = false;
                } else {
                    tilMaxParticipants.setError(null);
                }
            } catch (NumberFormatException e) {
                tilMaxParticipants.setError(getString(R.string.enter_valid_number));
                isValid = false;
            }
        }        // Xác thực mật khẩu nếu được yêu cầu
        if (switchPassword.isChecked() && TextUtils.isEmpty(getTextFromEditText(etPassword))) {
            tilPassword.setError(getString(R.string.password_cannot_be_empty));
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        // Xác thực lựa chọn quiz
        if (viewModel.getSelectedQuiz() == null) {
            showToast(getString(R.string.please_select_quiz));
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
                        showToast(getString(R.string.room_created_success));

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
                                resource.getMessage() : getString(R.string.failed_to_create_room));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (quizSelectionDialog != null) {
            quizSelectionDialog.dismiss();
        }
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