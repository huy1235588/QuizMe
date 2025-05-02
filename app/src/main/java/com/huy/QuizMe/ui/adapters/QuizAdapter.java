package com.huy.QuizMe.ui.adapters;

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

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    
    private final List<Quiz> quizzes;
    private final Context context;
    private OnQuizClickListener listener;

    public QuizAdapter(Context context) {
        this.context = context;
        this.quizzes = new ArrayList<>();
    }

    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz);
    }

    public void setOnQuizClickListener(OnQuizClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.tvQuizTitle.setText(quiz.getTitle());
        holder.tvQuizCategory.setText(quiz.getCategoryName());
        holder.tvQuestionCount.setText(String.format("%d câu hỏi", quiz.getQuestionCount()));
        
        // Set difficulty
        String difficulty = quiz.getDifficulty();
        if (difficulty != null) {
            holder.tvDifficulty.setText(difficulty);
            // Apply different colors based on difficulty
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
        
        // Load quiz thumbnail using Glide
        if (quiz.getQuizThumbnails() != null && !quiz.getQuizThumbnails().isEmpty()) {
            Glide.with(context)
                    .load(quiz.getQuizThumbnails())
                    .placeholder(R.drawable.placeholder_quiz)
                    .error(R.drawable.placeholder_quiz)
                    .into(holder.ivQuizImage);
        } else {
            holder.ivQuizImage.setImageResource(R.drawable.placeholder_quiz);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuizClick(quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public void updateQuizzes(List<Quiz> newQuizzes) {
        quizzes.clear();
        if (newQuizzes != null) {
            quizzes.addAll(newQuizzes);
        }
        notifyDataSetChanged();
    }

    public void addQuizzes(List<Quiz> moreQuizzes) {
        if (moreQuizzes != null) {
            int startPosition = quizzes.size();
            quizzes.addAll(moreQuizzes);
            notifyItemRangeInserted(startPosition, moreQuizzes.size());
        }
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
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