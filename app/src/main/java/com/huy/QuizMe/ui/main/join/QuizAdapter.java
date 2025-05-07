package com.huy.QuizMe.ui.main.join;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
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
        holder.bind(quiz, position == selectedPosition, v -> selectQuiz(holder.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    /**
     * Xử lý lựa chọn quiz
     * @param position Vị trí của quiz được chọn
     */
    private void selectQuiz(int position) {
        if (position == RecyclerView.NO_POSITION) return;
        
        int previousSelected = selectedPosition;
        selectedPosition = position;

        // Chỉ cập nhật các mục thay đổi để tối ưu hiệu suất
        if (previousSelected != -1) {
            notifyItemChanged(previousSelected);
        }
        notifyItemChanged(selectedPosition);

        // Thông báo cho listener
        if (listener != null && position < quizzes.size()) {
            listener.onQuizClicked(quizzes.get(position));
        }
    }

    /**
     * Cập nhật danh sách quiz và làm mới RecyclerView
     * @param newQuizzes Danh sách quiz mới
     */
    public void updateItems(List<Quiz> newQuizzes) {
        if (newQuizzes == null) return;

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new QuizDiffCallback(quizzes, newQuizzes));
        
        quizzes.clear();
        quizzes.addAll(newQuizzes);
        selectedPosition = -1; // Reset selected position
        
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * Cập nhật danh sách quiz từ PagedResponse và làm mới RecyclerView
     * @param pagedResponse đối tượng PagedResponse chứa danh sách Quiz
     */
    public void updateItems(PagedResponse<Quiz> pagedResponse) {
        if (pagedResponse == null || pagedResponse.getContent() == null) return;
        updateItems(pagedResponse.getContent());
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
        
        void bind(Quiz quiz, boolean isSelected, View.OnClickListener clickListener) {
            tvQuizTitle.setText(quiz.getTitle());
            
            // Đặt mô tả nếu có
            tvQuizDescription.setText(quiz.getDescription());
            tvQuizDescription.setVisibility(quiz.getDescription() != null && 
                                          !quiz.getDescription().isEmpty() ? 
                                          View.VISIBLE : View.GONE);
            
            // Đặt số lượng câu hỏi nếu có
            int questionCount = quiz.getQuestionCount();
            tvQuestionCount.setText(questionCount > 0 ? 
                                   questionCount + " Questions" : "");
            tvQuestionCount.setVisibility(questionCount > 0 ? View.VISIBLE : View.GONE);
            
            // Đặt trạng thái của nút radio
            rbSelect.setChecked(isSelected);
            
            // Đặt bộ lắng nghe sự kiện click cho toàn bộ item
            itemView.setOnClickListener(clickListener);
        }
    }
    
    /**
     * DiffUtil.Callback để tối ưu cập nhật RecyclerView
     */
    private static class QuizDiffCallback extends DiffUtil.Callback {
        private final List<Quiz> oldList;
        private final List<Quiz> newList;

        QuizDiffCallback(List<Quiz> oldList, List<Quiz> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldPosition, int newPosition) {
            return oldList.get(oldPosition).getId() == newList.get(newPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldPosition, int newPosition) {
            Quiz oldQuiz = oldList.get(oldPosition);
            Quiz newQuiz = newList.get(newPosition);
            return oldQuiz.getTitle().equals(newQuiz.getTitle()) && 
                  (oldQuiz.getDescription() == null ? newQuiz.getDescription() == null : 
                  oldQuiz.getDescription().equals(newQuiz.getDescription())) &&
                  oldQuiz.getQuestionCount() == newQuiz.getQuestionCount();
        }
    }
}