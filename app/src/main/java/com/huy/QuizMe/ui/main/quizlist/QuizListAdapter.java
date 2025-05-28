package com.huy.QuizMe.ui.main.quizlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.utils.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizViewHolder> {

    private Context context;
    private List<Quiz> quizzes;
    private OnQuizClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz);
    }

    public QuizListAdapter(Context context, List<Quiz> quizzes) {
        this.context = context;
        this.quizzes = quizzes;
    }

    public void setOnQuizClickListener(OnQuizClickListener listener) {
        this.listener = listener;
    }

    public void updateQuizzes(List<Quiz> newQuizzes) {
        this.quizzes.clear();
        this.quizzes.addAll(newQuizzes);
        notifyDataSetChanged();
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discover_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.bind(quiz);
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    class QuizViewHolder extends RecyclerView.ViewHolder {
        ImageView ivQuizBg;
        TextView tvQuizCount;
        TextView tvQuizTitle;
        CircleImageView ivAuthor;
        TextView tvAuthorName;

        QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            ivQuizBg = itemView.findViewById(R.id.iv_quiz_thumbnail);
            tvQuizCount = itemView.findViewById(R.id.tv_question_count);
            tvQuizTitle = itemView.findViewById(R.id.tv_quiz_title);
            ivAuthor = itemView.findViewById(R.id.iv_author);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onQuizClick(quizzes.get(position));
                }
            });
        }

        void bind(Quiz quiz) {
            tvQuizTitle.setText(quiz.getTitle());
            tvAuthorName.setText(quiz.getCreatorName());
            tvQuizCount.setText(quiz.getQuestionCount() + " Questions");

            // Load quiz thumbnail
            if (quiz.getQuizThumbnails() != null && !quiz.getQuizThumbnails().isEmpty()) {
                ImageLoader.loadImageWithTransformations(
                        context, ivQuizBg, quiz.getQuizThumbnails(), R.drawable.placeholder_quiz, R.drawable.placeholder_quiz);
            } else {
                ivQuizBg.setImageResource(R.drawable.bg_quiz);
            }

            ImageLoader.loadImageWithTransformations(context, ivAuthor, quiz.getQuizThumbnails(),
                    R.drawable.placeholder_quiz, R.drawable.placeholder_quiz);

        }
    }

    // Helper method to get relative time string (e.g., "2 months ago")
    private String getRelativeTimeSpanString(String dateString) {
        try {
            Date date = dateFormat.parse(dateString);
            long now = System.currentTimeMillis();
            long time = date.getTime();
            long diff = now - time;

            if (diff < TimeUnit.MINUTES.toMillis(1)) {
                return "just now";
            } else if (diff < TimeUnit.HOURS.toMillis(1)) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
            } else if (diff < TimeUnit.DAYS.toMillis(1)) {
                long hours = TimeUnit.MILLISECONDS.toHours(diff);
                return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
            } else if (diff < TimeUnit.DAYS.toMillis(30)) {
                long days = TimeUnit.MILLISECONDS.toDays(diff);
                return days + " day" + (days > 1 ? "s" : "") + " ago";
            } else {
                long months = TimeUnit.MILLISECONDS.toDays(diff) / 30;
                return months + " month" + (months > 1 ? "s" : "") + " ago";
            }
        } catch (Exception e) {
            return "";
        }
    }
}