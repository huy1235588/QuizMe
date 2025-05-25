package com.huy.QuizMe.data.model.game;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * DTO cho request gửi câu trả lời từ client
 */
public class AnswerRequest implements Serializable {
    @SerializedName("questionId")
    private Long questionId;

    @SerializedName("selectedOptions")
    private List<Long> selectedOptions;

    @SerializedName("textAnswer")
    private String textAnswer; // Dành cho TYPE_ANSWER

    @SerializedName("answerTime")
    private Long answerTime; // Thời gian trả lời (milliseconds)

    // Constructors
    public AnswerRequest() {
    }

    public AnswerRequest(Long questionId, List<Long> selectedOptions, String textAnswer, Long answerTime) {
        this.questionId = questionId;
        this.selectedOptions = selectedOptions;
        this.textAnswer = textAnswer;
        this.answerTime = answerTime;
    }

    // Getters and Setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public List<Long> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<Long> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public Long getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Long answerTime) {
        this.answerTime = answerTime;
    }
}
