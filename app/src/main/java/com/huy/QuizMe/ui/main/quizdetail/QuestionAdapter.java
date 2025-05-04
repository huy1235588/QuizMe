package com.huy.QuizMe.ui.main.quizdetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questions = new ArrayList<>();

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question, position + 1);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
        notifyDataSetChanged();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivQuestionTypeImage;
        private final TextView tvQuestionNumberAndType;
        private final TextView tvQuestionPreview;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivQuestionTypeImage = itemView.findViewById(R.id.iv_question_type_image);
            tvQuestionNumberAndType = itemView.findViewById(R.id.tv_question_number_and_type);
            tvQuestionPreview = itemView.findViewById(R.id.tv_question_preview);
        }

        public void bind(Question question, int position) {
            // Set question number and type
            String numberAndType = position + " - " + getQuestionTypeDisplay(question.getType());
            tvQuestionNumberAndType.setText(numberAndType);

            // Set question preview text
            tvQuestionPreview.setText(question.getContent());

            // Set question type image based on type
            int imageResource = getQuestionTypeImageResource(question.getType());
            ivQuestionTypeImage.setImageResource(imageResource);
            
            // You could also load images from a URL if available in your model
            if (question.getImageUrl() != null && !question.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(question.getImageUrl())
                        .placeholder(imageResource)
                        .error(imageResource)
                        .into(ivQuestionTypeImage);
            }
        }

        private String getQuestionTypeDisplay(String type) {
            switch (type) {
                case "multiple_choice":
                    return "Multiple Choice";
                case "true_false":
                    return "True/False";
                case "fill_blank":
                    return "Fill in the Blank";
                case "matching":
                    return "Matching";
                default:
                    return "Quiz";
            }
        }

        private int getQuestionTypeImageResource(String type) {
            // Return appropriate drawable resource based on question type
            // Replace with actual drawable resources for each type
            switch (type) {
                case "multiple_choice":
                    return R.drawable.img_question_type; // Replace with actual resource
                case "true_false":
                    return R.drawable.img_question_type; // Replace with actual resource
                case "fill_blank":
                    return R.drawable.img_question_type; // Replace with actual resource
                case "matching":
                    return R.drawable.img_question_type; // Replace with actual resource
                default:
                    return R.drawable.img_question_type; // Default question image
            }
        }
    }
}