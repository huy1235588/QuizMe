package com.huy.QuizMe.ui.main.createquiz;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Question;
import com.huy.QuizMe.data.model.QuestionOption;
import com.huy.QuizMe.data.model.Quiz;

import java.util.ArrayList;
import java.util.List;

public class AddQuestionFragment extends Fragment {

    // Khai báo các view trong header
    private ImageButton btnBack, btnMore;
    private TextView tvTitle;

    // Khai báo các view trong cover image section
    private CardView cardCoverImage;
    private ImageView ivCoverImage;
    private TextView tvAddCoverImage;

    // Khai báo các view trong options section
    private CardView cardQuizType, cardPoints, cardTime;
    private TextView tvQuiz, tvPoints, tvTime;
    private ImageView ivQuizDropdown, ivPointsDropdown, ivTimeDropdown;

    // Khai báo các view cho question và answers
    private CardView cardQuestion, cardAnswer1, cardAnswer2, cardAnswer3, cardAnswer4;
    private TextView tvQuestionHint, tvAnswer1Hint, tvAnswer2Hint, tvAnswer3Hint, tvAnswer4Hint;

    // Khai báo bottom bar
    private CardView cardQuestionNumber;
    private FloatingActionButton fabAddQuestion;
    private TextView tvQuestionNumberDisplay;

    // Thuộc tính câu hỏi hiện tại
    private int currentQuestionNumber = 1;
    private int timeLimit = 10; // giây
    private int points = 100;
    private int correctAnswerIndex = 0; // chỉ số 0-based (mặc định: câu trả lời đầu tiên là đúng)
    private String currentQuestionText = "";
    private String[] answerTexts = new String[4];
    private Uri selectedImageUri = null;

    // Dữ liệu quiz
    private Quiz quiz;
    private List<Question> questions = new ArrayList<>();

    // Request code cho chọn ảnh
    private static final int PICK_IMAGE_REQUEST = 1;

    /**
     * Tạo instance mới của AddQuestionFragment với quiz
     */
    public static AddQuestionFragment newInstance(Quiz quiz) {
        AddQuestionFragment fragment = new AddQuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable("quiz", quiz);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lấy quiz từ arguments
        if (getArguments() != null) {
            quiz = (Quiz) getArguments().getSerializable("quiz");
        }
        // Khởi tạo mảng câu trả lời
        for (int i = 0; i < answerTexts.length; i++) {
            answerTexts[i] = "";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners();
        updateUI();
    }

    /**
     * Khởi tạo tất cả các view
     */
    private void initViews(View view) {
        // Khởi tạo header views
        btnBack = view.findViewById(R.id.btnBack);
        btnMore = view.findViewById(R.id.btnMore);
        tvTitle = view.findViewById(R.id.tvTitle);

        // Khởi tạo cover image views
        cardCoverImage = view.findViewById(R.id.cardCoverImage);
        ivCoverImage = view.findViewById(R.id.ivCoverImage);
        tvAddCoverImage = view.findViewById(R.id.tvAddCoverImage);

        // Khởi tạo options views
        cardQuizType = view.findViewById(R.id.cardQuizType);
        cardPoints = view.findViewById(R.id.cardPoints);
        cardTime = view.findViewById(R.id.cardTime);
        tvQuiz = view.findViewById(R.id.tvQuiz);
        tvPoints = view.findViewById(R.id.tvPoints);
        tvTime = view.findViewById(R.id.tvTime);
        ivQuizDropdown = view.findViewById(R.id.ivQuizDropdown);
        ivPointsDropdown = view.findViewById(R.id.ivPointsDropdown);
        ivTimeDropdown = view.findViewById(R.id.ivTimeDropdown);

        // Khởi tạo question và answer views
        cardQuestion = view.findViewById(R.id.cardQuestion);
        cardAnswer1 = view.findViewById(R.id.cardAnswer1);
        cardAnswer2 = view.findViewById(R.id.cardAnswer2);
        cardAnswer3 = view.findViewById(R.id.cardAnswer3);
        cardAnswer4 = view.findViewById(R.id.cardAnswer4);
        tvQuestionHint = view.findViewById(R.id.tvQuestionHint);
        tvAnswer1Hint = view.findViewById(R.id.tvAnswer1Hint);
        tvAnswer2Hint = view.findViewById(R.id.tvAnswer2Hint);
        tvAnswer3Hint = view.findViewById(R.id.tvAnswer3Hint);
        tvAnswer4Hint = view.findViewById(R.id.tvAnswer4Hint);

        // Khởi tạo bottom bar views
        cardQuestionNumber = view.findViewById(R.id.cardQuestionNumber);
        fabAddQuestion = view.findViewById(R.id.fabAddQuestion);

        // Tìm TextView hiển thị số câu hỏi trong cardQuestionNumber
        tvQuestionNumberDisplay = cardQuestionNumber.findViewById(android.R.id.text1);
    }

    /**
     * Thiết lập các listener cho các view
     */
    private void setupListeners() {
        // Listener cho nút back
        btnBack.setOnClickListener(v -> {
            // Hiển thị dialog xác nhận nếu có thay đổi
            if (hasChanges()) {
                showExitConfirmationDialog();
            } else {
                requireActivity().onBackPressed();
            }
        });

        // Listener cho nút more (menu thêm tùy chọn)
        btnMore.setOnClickListener(v -> showMoreOptionsMenu());

        // Listener cho cover image
        cardCoverImage.setOnClickListener(v -> selectCoverImage());

        // Listener cho dropdown quiz type
        cardQuizType.setOnClickListener(v -> showQuizTypeOptions());

        // Listener cho dropdown points
        cardPoints.setOnClickListener(v -> showPointsOptions());

        // Listener cho dropdown time
        cardTime.setOnClickListener(v -> showTimeOptions());

        // Listener cho question card
        cardQuestion.setOnClickListener(v -> showQuestionInputDialog());

        // Listener cho answer cards
        cardAnswer1.setOnClickListener(v -> showAnswerInputDialog(0));
        cardAnswer2.setOnClickListener(v -> showAnswerInputDialog(1));
        cardAnswer3.setOnClickListener(v -> showAnswerInputDialog(2));
        cardAnswer4.setOnClickListener(v -> showAnswerInputDialog(3));

        // Long click để chọn đáp án đúng
        cardAnswer1.setOnLongClickListener(v -> {
            setCorrectAnswer(0);
            return true;
        });

        cardAnswer2.setOnLongClickListener(v -> {
            setCorrectAnswer(1);
            return true;
        });

        cardAnswer3.setOnLongClickListener(v -> {
            setCorrectAnswer(2);
            return true;
        });

        cardAnswer4.setOnLongClickListener(v -> {
            setCorrectAnswer(3);
            return true;
        });

        // Listener cho FAB add question
        fabAddQuestion.setOnClickListener(v -> saveCurrentAndAddNew());
    }

    /**
     * Cập nhật giao diện người dùng
     */
    private void updateUI() {
        // Cập nhật số câu hỏi
        if (tvQuestionNumberDisplay != null) {
            tvQuestionNumberDisplay.setText(String.valueOf(currentQuestionNumber));
        }

        // Cập nhật text các dropdown
        tvPoints.setText(points + " points");
        tvTime.setText(timeLimit + " seconds");

        // Cập nhật hiển thị question
        if (currentQuestionText.isEmpty()) {
            tvQuestionHint.setText("Tap to add question");
            tvQuestionHint.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            tvQuestionHint.setText(currentQuestionText);
            tvQuestionHint.setTextColor(getResources().getColor(android.R.color.black));
        }

        // Cập nhật hiển thị answers
        updateAnswerDisplay(0, tvAnswer1Hint);
        updateAnswerDisplay(1, tvAnswer2Hint);
        updateAnswerDisplay(2, tvAnswer3Hint);
        updateAnswerDisplay(3, tvAnswer4Hint);

        // Cập nhật màu sắc cho đáp án đúng
        updateCorrectAnswerHighlight();
    }

    /**
     * Cập nhật hiển thị một đáp án
     */
    private void updateAnswerDisplay(int index, TextView textView) {
        if (answerTexts[index].isEmpty()) {
            textView.setText("Tap to add answer");
            textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            textView.setText(answerTexts[index]);
            textView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    /**
     * Cập nhật highlight cho đáp án đúng
     */
    private void updateCorrectAnswerHighlight() {
        // Reset tất cả cards về màu mặc định
        cardAnswer1.setCardBackgroundColor(getResources().getColor(R.color.light_gray, null));
        cardAnswer2.setCardBackgroundColor(getResources().getColor(R.color.light_gray, null));
        cardAnswer3.setCardBackgroundColor(getResources().getColor(R.color.light_gray, null));
        cardAnswer4.setCardBackgroundColor(getResources().getColor(R.color.light_gray, null));

        // Highlight đáp án đúng
        CardView correctCard = null;
        switch (correctAnswerIndex) {
            case 0:
                correctCard = cardAnswer1;
                break;
            case 1:
                correctCard = cardAnswer2;
                break;
            case 2:
                correctCard = cardAnswer3;
                break;
            case 3:
                correctCard = cardAnswer4;
                break;
        }

        if (correctCard != null && !answerTexts[correctAnswerIndex].isEmpty()) {
            correctCard.setCardBackgroundColor(getResources().getColor(R.color.light_green, null));
        }
    }

    /**
     * Hiển thị dialog chọn thời gian
     */
    private void showTimeOptions() {
        final String[] timeOptions = {"5 giây", "10 giây", "15 giây", "20 giây", "30 giây", "60 giây"};
        final int[] timeValues = {5, 10, 15, 20, 30, 60};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn thời gian trả lời")
                .setItems(timeOptions, (dialog, which) -> {
                    timeLimit = timeValues[which];
                    updateUI();
                })
                .show();
    }

    /**
     * Hiển thị dialog chọn điểm số
     */
    private void showPointsOptions() {
        final String[] pointOptions = {"50 điểm", "100 điểm", "200 điểm", "300 điểm", "400 điểm", "500 điểm"};
        final int[] pointValues = {50, 100, 200, 300, 400, 500};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn điểm số")
                .setItems(pointOptions, (dialog, which) -> {
                    points = pointValues[which];
                    updateUI();
                })
                .show();
    }

    /**
     * Hiển thị dialog chọn loại quiz
     */
    private void showQuizTypeOptions() {
        final String[] quizTypes = {"Quiz", "Đúng/Sai", "Bỏ phiếu"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn loại câu hỏi")
                .setItems(quizTypes, (dialog, which) -> {
                    tvQuiz.setText(quizTypes[which]);
                    // Điều chỉnh UI dựa trên loại quiz nếu cần
                    if (which == 1) { // Đúng/Sai
                        // Ẩn answer 3 và 4, chỉ hiển thị 2 đáp án
                        answerTexts[0] = "Đúng";
                        answerTexts[1] = "Sai";
                        answerTexts[2] = "";
                        answerTexts[3] = "";
                        updateUI();
                    }
                })
                .show();
    }

    /**
     * Hiển thị menu thêm tùy chọn
     */
    private void showMoreOptionsMenu() {
        final String[] options = {"Lưu và kết thúc", "Xem trước", "Cài đặt nâng cao"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Tùy chọn thêm")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Lưu và kết thúc
                            finishAndSaveQuiz();
                            break;
                        case 1: // Xem trước
                            previewQuiz();
                            break;
                        case 2: // Cài đặt nâng cao
                            showAdvancedSettings();
                            break;
                    }
                })
                .show();
    }

    /**
     * Chọn ảnh cover
     */
    private void selectCoverImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Hiển thị dialog nhập câu hỏi
     */
    private void showQuestionInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Nhập câu hỏi");

        // Tạo EditText
        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setText(currentQuestionText);
        input.setHint("Nhập câu hỏi của bạn...");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            currentQuestionText = input.getText().toString().trim();
            updateUI();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Hiển thị dialog nhập đáp án
     */
    private void showAnswerInputDialog(int answerIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Nhập đáp án " + (answerIndex + 1));

        // Tạo EditText
        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setText(answerTexts[answerIndex]);
        input.setHint("Nhập đáp án...");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            answerTexts[answerIndex] = input.getText().toString().trim();
            updateUI();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        // Nếu đáp án đã có nội dung, thêm nút xóa
        if (!answerTexts[answerIndex].isEmpty()) {
            builder.setNeutralButton("Xóa", (dialog, which) -> {
                answerTexts[answerIndex] = "";
                updateUI();
            });
        }

        builder.show();
    }

    /**
     * Đặt đáp án đúng
     */
    private void setCorrectAnswer(int answerIndex) {
        if (!answerTexts[answerIndex].isEmpty()) {
            correctAnswerIndex = answerIndex;
            updateUI();
            Toast.makeText(requireContext(), "Đã đặt làm đáp án đúng", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Vui lòng nhập nội dung đáp án trước", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Kiểm tra có thay đổi nào chưa
     */
    private boolean hasChanges() {
        return !currentQuestionText.isEmpty() ||
                !answerTexts[0].isEmpty() ||
                !answerTexts[1].isEmpty() ||
                !answerTexts[2].isEmpty() ||
                !answerTexts[3].isEmpty() ||
                selectedImageUri != null;
    }

    /**
     * Hiển thị dialog xác nhận thoát
     */
    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận thoát")
                .setMessage("Bạn có thay đổi chưa lưu. Bạn có muốn thoát không?")
                .setPositiveButton("Thoát", (dialog, which) -> requireActivity().onBackPressed())
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Lưu câu hỏi hiện tại và thêm câu hỏi mới
     */
    private void saveCurrentAndAddNew() {
        // Kiểm tra tính hợp lệ của câu hỏi và đáp án
        if (currentQuestionText.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đếm số đáp án có nội dung
        int answersWithContent = 0;
        for (String answer : answerTexts) {
            if (!answer.isEmpty()) {
                answersWithContent++;
            }
        }

        if (answersWithContent < 2) {
            Toast.makeText(requireContext(), "Cần ít nhất 2 đáp án", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra đáp án đúng có nội dung không
        if (answerTexts[correctAnswerIndex].isEmpty()) {
            Toast.makeText(requireContext(), "Đáp án đúng không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo và lưu câu hỏi
        Question question = new Question();
        question.setContent(currentQuestionText);
        question.setTimeLimit(timeLimit);
        question.setPoints(points);
        question.setType("QUIZ");

        // Tạo các tùy chọn câu hỏi
        List<QuestionOption> options = new ArrayList<>();

        for (int i = 0; i < answerTexts.length; i++) {
            if (!answerTexts[i].isEmpty()) {
                QuestionOption option = new QuestionOption();
                option.setContent(answerTexts[i]);
                option.setIsCorrect(i == correctAnswerIndex);
                options.add(option);
            }
        }

        question.setOptions(options);

        // Thêm vào danh sách câu hỏi
        questions.add(question);

        // Xóa inputs và tăng số câu hỏi
        clearInputs();
        currentQuestionNumber++;
        updateUI();

        Toast.makeText(requireContext(), "Đã thêm câu hỏi thành công", Toast.LENGTH_SHORT).show();
    }

    /**
     * Xóa tất cả inputs
     */
    private void clearInputs() {
        currentQuestionText = "";
        for (int i = 0; i < answerTexts.length; i++) {
            answerTexts[i] = "";
        }

        // Reset giá trị mặc định
        correctAnswerIndex = 0;
        selectedImageUri = null;
    }

    /**
     * Hoàn thành và lưu quiz
     */
    private void finishAndSaveQuiz() {
        // Lưu câu hỏi hiện tại nếu hợp lệ
        if (!currentQuestionText.isEmpty()) {
            saveCurrentAndAddNew();
        }

        if (questions.isEmpty()) {
            Toast.makeText(requireContext(), "Tạo ít nhất một câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đặt câu hỏi cho quiz
        // quiz.setQuestions(questions);

        // TODO: Lưu quiz vào database hoặc chuyển đến màn hình xem trước

        Toast.makeText(requireContext(), "Đã lưu quiz thành công!", Toast.LENGTH_SHORT).show();

        // Quay lại
        requireActivity().onBackPressed();
    }

    /**
     * Xem trước quiz
     */
    private void previewQuiz() {
        if (questions.isEmpty() && currentQuestionText.isEmpty()) {
            Toast.makeText(requireContext(), "Chưa có câu hỏi nào để xem trước", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Mở màn hình xem trước quiz
        Toast.makeText(requireContext(), "Tính năng xem trước sẽ sớm có", Toast.LENGTH_SHORT).show();
    }

    /**
     * Hiển thị cài đặt nâng cao
     */
    private void showAdvancedSettings() {
        // TODO: Mở màn hình cài đặt nâng cao
        Toast.makeText(requireContext(), "Cài đặt nâng cao sẽ sớm có", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Hiển thị ảnh đã chọn
                ivCoverImage.setImageURI(selectedImageUri);
                tvAddCoverImage.setText("Đã chọn ảnh");
                tvAddCoverImage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Lưu trạng thái hiện tại nếu cần
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Dọn dẹp resources nếu cần
    }
}
