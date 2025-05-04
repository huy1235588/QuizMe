package com.huy.QuizMe.ui.quiz.adapters;

import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentActivity;
import androidx.annotation.NonNull;

import android.content.Context;

import com.huy.QuizMe.data.model.Question;
import com.huy.QuizMe.ui.quiz.fragments.MultipleChoiceFragment;

import java.util.List;

public class QuestionAdapter extends FragmentStateAdapter {
    private final Context context;
    private final List<Question> questions;

    public QuestionAdapter(FragmentActivity fragmentActivity, List<Question> questions) {
        super(fragmentActivity);
        this.context = fragmentActivity;
        this.questions = questions;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Question question = questions.get(position);

        // Tạo fragment phù hợp dựa trên loại câu hỏi
        switch (question.getType()) {
            case "MULTIPLE_CHOICE":
                return MultipleChoiceFragment.newInstance(question);
//            case "TRUE_FALSE":
//                return TrueFalseFragment.newInstance(question);
//            case "TEXT_ANSWER":
//                return TypeAnswerFragment.newInstance(question);
            default:
                return MultipleChoiceFragment.newInstance(question);
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
