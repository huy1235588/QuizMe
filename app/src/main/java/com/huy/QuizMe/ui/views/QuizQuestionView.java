package com.huy.QuizMe.ui.views;

import android.content.Context;
import android.graphics.Color;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Question;

public class QuizQuestionView extends QuestionView {
    private ImageView questionImage;
    private TextView questionText;
    private Button option1, option2, option3, option4;
    private String selectedAnswer;
    private Question question;

    public QuizQuestionView(Context context) {
        super(context);
        inflate(context, R.layout.quiz_question_quiz, this);
        questionImage = findViewById(R.id.question_image);
        questionText = findViewById(R.id.question_text);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        option1.setOnClickListener(v -> selectOption(option1));
        option2.setOnClickListener(v -> selectOption(option2));
        option3.setOnClickListener(v -> selectOption(option3));
        option4.setOnClickListener(v -> selectOption(option4));
    }

    private void selectOption(Button button) {
        selectedAnswer = button.getText().toString();
        if (listener != null) {
            listener.onAnswerSelected(selectedAnswer);
        }
    }

    @Override
    public void setQuestion(Question question) {
        this.question = question;
        // Load image using Picasso or Glide
//        Picasso.get().load(question.getImageUrl()).into(questionImage);
        questionText.setText(question.getContent());
//        option1.setText(question.getOptions().get(0).getContent());
//        option2.setText(question.getOptions().get(1).getContent());
//        option3.setText(question.getOptions().get(2).getContent());
//        option4.setText(question.getOptions().get(3).getContent());
    }

    @Override
    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    @Override
    public void showFeedback(boolean isCorrect) {
        String correctAnswer = question.getCorrectAnswer();
        if (isCorrect) {
            setButtonColor(getButtonByText(selectedAnswer), Color.GREEN);
        } else {
            setButtonColor(getButtonByText(selectedAnswer), Color.RED);
            setButtonColor(getButtonByText(correctAnswer), Color.GREEN);
        }
        // Disable buttons
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);
    }

    private Button getButtonByText(String text) {
        if (option1.getText().equals(text)) return option1;
        if (option2.getText().equals(text)) return option2;
        if (option3.getText().equals(text)) return option3;
        if (option4.getText().equals(text)) return option4;
        return null;
    }

    private void setButtonColor(Button button, int color) {
        button.setBackgroundColor(color);
    }
}
