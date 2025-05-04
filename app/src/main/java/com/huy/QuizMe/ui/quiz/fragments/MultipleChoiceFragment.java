package com.huy.QuizMe.ui.quiz.fragments;

import androidx.fragment.app.Fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Question;
import com.huy.QuizMe.databinding.FragmentMultipleChoiceBinding;
import com.huy.QuizMe.ui.quiz.QuizPlayActivity;
import com.huy.QuizMe.ui.quiz.QuizPlayViewModel;
import com.huy.QuizMe.utils.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultipleChoiceFragment extends Fragment {
    private FragmentMultipleChoiceBinding binding;
    private Question question;
    private QuizPlayViewModel viewModel;
    private final List<Button> optionButtons = new ArrayList<>();
    private boolean answered = false;
    private OnAnswerSelectedListener listener;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int selectedButtonIndex = -1;

    /**
     * Tạo một instance mới của MultipleChoiceFragment với câu hỏi đã cho
     * 
     * @param question Câu hỏi trắc nghiệm
     * @return Fragment instance
     */
    public static MultipleChoiceFragment newInstance(Question question) {
        MultipleChoiceFragment fragment = new MultipleChoiceFragment();
        Bundle args = new Bundle();
        args.putSerializable("question", (Serializable) question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable("question");
        }

        if (getActivity() instanceof QuizPlayActivity) {
            viewModel = ((QuizPlayActivity) getActivity()).getViewModel();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        binding = FragmentMultipleChoiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupQuestion();
        setupAnswerOptions();
        
        // Kiểm tra xem người dùng đã trả lời câu hỏi này chưa
        if (question != null && viewModel != null && viewModel.hasAnsweredQuestion(question.getId())) {
            answered = true;
            // Khôi phục trạng thái giao diện (tùy chọn)
        }
        
        // Animation hiển thị câu hỏi
        animateQuestionAppear();
    }

    private void setupQuestion() {
        // Thiết lập nội dung câu hỏi
        if (question != null) {
            binding.tvQuestionText.setText(question.getContent());

            // Hiển thị hình ảnh nếu có
            if (question.getImageUrl() != null && !question.getImageUrl().isEmpty()) {
                binding.cardQuestionImage.setVisibility(View.VISIBLE);
                ImageLoader.loadImage(requireContext(), binding.ivQuestionImage,
                        question.getImageUrl(), R.drawable.placeholder_image, R.drawable.placeholder_image);
                
                // Cho phép xem ảnh full màn hình khi click
                binding.ivQuestionImage.setOnClickListener(v -> showFullscreenImage(question.getImageUrl()));
            } else {
                binding.cardQuestionImage.setVisibility(View.GONE);
            }
        }

        // Khởi tạo các nút tùy chọn
        optionButtons.add(binding.btnOptionA);
        optionButtons.add(binding.btnOptionB);
        optionButtons.add(binding.btnOptionC);
        optionButtons.add(binding.btnOptionD);
    }

    private void showFullscreenImage(String imageUrl) {
        // Hiển thị hình ảnh full màn hình (có thể implement sau)
        // Ví dụ: mở một dialog hoặc activity mới để hiển thị ảnh lớn hơn
    }

    private void setupAnswerOptions() {
        if (question == null) return;
        
        // Lấy các tùy chọn và xáo trộn chúng
        List<String> options = new ArrayList<>();
        String correctAnswer = question.getCorrectAnswer();
        
        if (correctAnswer != null) {
            options.add(correctAnswer); // Đáp án đúng
            options.addAll(question.getWrongAnswers()); // Các đáp án sai
            Collections.shuffle(options);
        }

        // Gán các tùy chọn vào các nút
        for (int i = 0; i < optionButtons.size(); i++) {
            if (i < options.size()) {
                Button button = optionButtons.get(i);
                String option = options.get(i);

                button.setText(option);
                button.setVisibility(View.VISIBLE);
                
                // Thiết lập animation khi hover
                setupButtonHoverEffect(button);
                
                // Set OnClickListener
                final int buttonIndex = i;
                button.setOnClickListener(v -> selectAnswer(option, button, buttonIndex));
            } else {
                optionButtons.get(i).setVisibility(View.GONE);
            }
        }
        
        // Thiết lập màu sắc khác nhau cho các nút theo thiết kế
        if (optionButtons.size() >= 4) {
            // Sử dụng các nền màu khác nhau thay vì chỉ thay đổi khi trả lời
            optionButtons.get(0).setBackgroundResource(R.drawable.bg_option_button);       // Blue
            optionButtons.get(1).setBackgroundResource(R.drawable.bg_option_button_red);    // Red
            optionButtons.get(2).setBackgroundResource(R.drawable.bg_option_button_orange); // Orange
            optionButtons.get(3).setBackgroundResource(R.drawable.bg_option_button_green);  // Green
        }
        
        // Animation cho các nút
        animateOptions();
    }
    
    private void setupButtonHoverEffect(Button button) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    if (!answered) {
                        v.setScaleX(0.95f);
                        v.setScaleY(0.95f);
                    }
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.setScaleX(1.0f);
                    v.setScaleY(1.0f);
                    break;
            }
            // Trả về false để không can thiệp vào sự kiện click
            return false;
        });
    }
    
    private void animateQuestionAppear() {
        // Hiệu ứng cho tiêu đề câu hỏi
        binding.tvQuestionText.setAlpha(0f);
        binding.tvQuestionText.setTranslationY(-50f);
        binding.tvQuestionText.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        
        // Hiệu ứng cho hình ảnh
        if (binding.cardQuestionImage.getVisibility() == View.VISIBLE) {
            binding.cardQuestionImage.setAlpha(0f);
            binding.cardQuestionImage.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .setStartDelay(200)
                    .start();
        }
    }
    
    private void animateOptions() {
        for (int i = 0; i < optionButtons.size(); i++) {
            Button button = optionButtons.get(i);
            if (button.getVisibility() == View.VISIBLE) {
                button.setAlpha(0f);
                button.setTranslationX(50f);
                button.animate()
                        .alpha(1f)
                        .translationX(0f)
                        .setDuration(300)
                        .setStartDelay(200 + (i * 100)) // Hiệu ứng tuần tự
                        .start();
            }
        }
    }
    
    /**
     * Chọn một đáp án (nhưng chưa gửi kết quả)
     */
    private void selectAnswer(String selectedAnswer, Button selectedButton, int buttonIndex) {
        if (answered) return;
        
        // Reset trạng thái tất cả các nút
        resetAllButtons();
        
        // Highlight nút được chọn
        selectedButtonIndex = buttonIndex;
        
        // Thêm dấu check vào bên phải nút (tùy chọn, có thể thêm ImageView vào layout)
        addCheckMarkToButton(selectedButton);
        
        // Thông báo cho Activity biết người dùng đã sẵn sàng gửi câu trả lời
        if (listener != null) {
            boolean isCorrect = selectedAnswer.equals(question.getCorrectAnswer());
            viewModel.saveUserAnswer(question.getId(), selectedAnswer, isCorrect);
            listener.onAnswerSelected(isCorrect);
        }
    }
    
    private void addCheckMarkToButton(Button button) {
        // Đây là một phiên bản đơn giản, bạn có thể muốn thêm một ImageView vào layout của button
        // Trong ví dụ này, chúng tôi thay đổi nền của nút để hiển thị trạng thái được chọn
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
    }
    
    private void resetAllButtons() {
        for (Button button : optionButtons) {
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void animateSelectedButton(Button button, boolean isCorrect) {
        // Animation khi chọn nút
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.1f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(400);
        animatorSet.start();
    }
    
    private void showCorrectAnswerFeedback(Button selectedButton) {
        // Set background màu xanh cho đáp án đúng - không cần thay đổi
        // Đã gán màu cho các nút theo thiết kế mới
        
        // Hiển thị điểm thưởng
        binding.tvScore.setVisibility(View.VISIBLE);
        binding.tvScore.setAlpha(0f);
        binding.tvScore.setScaleX(0.5f);
        binding.tvScore.setScaleY(0.5f);
        binding.tvScore.animate()
                .alpha(1f)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(300)
                .withEndAction(() -> {
                    binding.tvScore.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .start();
                })
                .start();
    }
    
    public void setOnAnswerSelectedListener(OnAnswerSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(boolean isCorrect);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }
}
