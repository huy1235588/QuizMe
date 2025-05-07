package com.huy.QuizMe.ui.main.join;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.data.model.PagedResponse;

import java.util.ArrayList;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private final Context context;
    private final List<Quiz> quizzes;
    private OnQuizClickListener listener;
    private int selectedPosition = -1; // Mặc định không có lựa chọn nào

    public QuizAdapter(Context context) {
        this.context = context;
        this.quizzes = new ArrayList<>();
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz_selection, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.tvQuizTitle.setText(quiz.getTitle());
        
        // Đặt mô tả nếu có
        if (quiz.getDescription() != null && !quiz.getDescription().isEmpty()) {
            holder.tvQuizDescription.setText(quiz.getDescription());
            holder.tvQuizDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvQuizDescription.setVisibility(View.GONE);
        }
        
        // Đặt số lượng câu hỏi nếu có
        if (quiz.getQuestionCount() > 0) {
            holder.tvQuestionCount.setText(quiz.getQuestionCount()+ " Questions");
            holder.tvQuestionCount.setVisibility(View.VISIBLE);
        } else {
            holder.tvQuestionCount.setVisibility(View.GONE);
        }
        
        // Đặt trạng thái của nút radio
        holder.rbSelect.setChecked(position == selectedPosition);
        
        // Đặt bộ lắng nghe sự kiện click cho toàn bộ item
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();
            
            // Thông báo vị trí trước và mới thay đổi để làm mới UI
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            
            // Thông báo cho listener
            if (listener != null) {
                listener.onQuizClicked(quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    /**
     * Cập nhật danh sách quiz và làm mới RecyclerView
     * @param newQuizzes Danh sách quiz mới
     */
    public void updateItems(List<Quiz> newQuizzes) {
        quizzes.clear();
        if (newQuizzes != null) {
            quizzes.addAll(newQuizzes);
        }
        notifyDataSetChanged();
    }

    /**
     * Cập nhật danh sách quiz từ PagedResponse và làm mới RecyclerView
     * @param pagedResponse đối tượng PagedResponse chứa danh sách Quiz
     */
    public void updateItems(PagedResponse<Quiz> pagedResponse) {
        quizzes.clear();
        if (pagedResponse != null && pagedResponse.getContent() != null) {
            quizzes.addAll(pagedResponse.getContent());
        }
        selectedPosition = -1; // Reset selected position
        notifyDataSetChanged();
    }

    /**
     * Thiết lập listener cho sự kiện chọn quiz
     * @param listener Listener cần thiết lập
     */
    public void setOnQuizClickListener(OnQuizClickListener listener) {
        this.listener = listener;
    }

    /**
     * Interface cho sự kiện click vào quiz
     */
    public interface OnQuizClickListener {
        void onQuizClicked(Quiz quiz);
    }

    /**
     * ViewHolder cho các mục Quiz
     */
    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizTitle;
        TextView tvQuizDescription;
        TextView tvQuestionCount;
        RadioButton rbSelect;

        QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizTitle = itemView.findViewById(R.id.tv_quiz_title);
            tvQuizDescription = itemView.findViewById(R.id.tv_quiz_description);
            tvQuestionCount = itemView.findViewById(R.id.tv_question_count);
            rbSelect = itemView.findViewById(R.id.rb_select_quiz);
        }
    }
}