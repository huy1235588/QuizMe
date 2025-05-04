package com.huy.QuizMe.ui.views;

import android.content.Context;
import android.widget.LinearLayout;

import com.huy.QuizMe.data.model.Question;

public abstract class QuestionView extends LinearLayout {
    public QuestionView(Context context) {
        super(context);
    }

    public abstract void setQuestion(Question question);

    public abstract String getSelectedAnswer();

    public abstract void showFeedback(boolean isCorrect);

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(String answer);
    }

    protected OnAnswerSelectedListener listener;

    public void setOnAnswerSelectedListener(OnAnswerSelectedListener listener) {
        this.listener = listener;
    }
}
