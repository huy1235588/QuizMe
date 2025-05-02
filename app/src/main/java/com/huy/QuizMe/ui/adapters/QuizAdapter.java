package com.huy.QuizMe.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Quiz;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter quản lý và hiển thị danh sách các quiz trong RecyclerView
 */
public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    // Danh sách các quiz được hiển thị
    private final List<Quiz> quizzes;
    // Context để load ảnh và tài nguyên
    private final Context context;
    // Listener xử lý sự kiện click vào quiz
    private OnQuizClickListener listener;

    /**
     * Khởi tạo adapter với context
     *
     * @param context Context của activity hoặc fragment
     */
    public QuizAdapter(Context context) {
        this.context = context;
        this.quizzes = new ArrayList<>();
    }

    /**
     * Interface xử lý sự kiện khi người dùng click vào một quiz
     */
    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz);
    }

    /**
     * Thiết lập listener cho sự kiện click vào quiz
     *
     * @param listener Listener xử lý sự kiện
     */
    public void setOnQuizClickListener(OnQuizClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view mới từ layout item_quiz
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);

        // Thiết lập thông tin cơ bản của quiz
        holder.tvQuizTitle.setText(quiz.getTitle());
        holder.tvQuizCategory.setText(quiz.getCategoryName());
        holder.tvQuestionCount.setText(String.format("%d câu hỏi", quiz.getQuestionCount()));

        // Thiết lập độ khó và màu sắc tương ứng
        setDifficultyWithColor(holder, quiz.getDifficulty());

        // Tải ảnh thumbnail của quiz
        loadQuizThumbnail(holder.ivQuizImage, quiz.getQuizThumbnails());

        // Xử lý sự kiện click vào item quiz
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuizClick(quiz);
            }
        });
    }

    /**
     * Thiết lập độ khó và màu sắc tương ứng
     *
     * @param holder     ViewHolder chứa view hiển thị độ khó
     * @param difficulty Chuỗi thể hiện độ khó (EASY, MEDIUM, HARD)
     */
    private void setDifficultyWithColor(@NonNull QuizViewHolder holder, String difficulty) {
        if (difficulty != null) {
            holder.tvDifficulty.setText(difficulty);

            // Áp dụng màu sắc dựa trên độ khó
            int colorResId;
            switch (difficulty.toUpperCase()) {
                case "EASY":
                    colorResId = R.color.difficulty_easy;
                    break;
                case "MEDIUM":
                    colorResId = R.color.difficulty_medium;
                    break;
                case "HARD":
                    colorResId = R.color.difficulty_hard;
                    break;
                default:
                    colorResId = R.color.difficulty_medium;
                    break;
            }
            holder.tvDifficulty.setBackgroundResource(colorResId);
        }
    }

    /**
     * Tải ảnh thumbnail của quiz sử dụng thư viện Glide
     *
     * @param imageView View để hiển thị ảnh
     * @param imageUrl  Đường dẫn ảnh cần tải
     */
    private void loadQuizThumbnail(ImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_quiz)
                    .error(R.drawable.placeholder_quiz)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder_quiz);
        }
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    /**
     * Cập nhật toàn bộ danh sách quiz và thông báo thay đổi
     *
     * @param newQuizzes Danh sách quiz mới
     */
    public void updateQuizzes(List<Quiz> newQuizzes) {
        quizzes.clear();
        if (newQuizzes != null) {
            quizzes.addAll(newQuizzes);
        }
        notifyDataSetChanged();
    }

    /**
     * Thêm quiz vào danh sách hiện tại (sử dụng cho tính năng tải thêm)
     *
     * @param moreQuizzes Danh sách quiz bổ sung
     */
    public void addQuizzes(List<Quiz> moreQuizzes) {
        if (moreQuizzes != null && !moreQuizzes.isEmpty()) {
            int startPosition = quizzes.size();
            quizzes.addAll(moreQuizzes);
            notifyItemRangeInserted(startPosition, moreQuizzes.size());
        }
    }

    /**
     * ViewHolder chứa các view thành phần trong item quiz
     */
    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        ImageView ivQuizImage;
        TextView tvQuizTitle;
        TextView tvQuizCategory;
        TextView tvQuestionCount;
        TextView tvDifficulty;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            ivQuizImage = itemView.findViewById(R.id.iv_quiz_image);
            tvQuizTitle = itemView.findViewById(R.id.tv_quiz_title);
            tvQuizCategory = itemView.findViewById(R.id.tv_quiz_category);
            tvQuestionCount = itemView.findViewById(R.id.tv_question_count);
            tvDifficulty = itemView.findViewById(R.id.tv_difficulty);
        }
    }
}