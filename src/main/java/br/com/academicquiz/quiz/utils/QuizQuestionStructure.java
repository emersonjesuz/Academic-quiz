package br.com.academicquiz.quiz.utils;

public class QuizQuestionStructure {
    private Long questionId;
    private Long alternativeId;
    private Boolean isCorrect;
    private Long alternativeCorrectId;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getAlternativeId() {
        return alternativeId;
    }

    public void setAlternativeId(Long alternativeId) {
        this.alternativeId = alternativeId;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Long getAlternativeCorrectId() {
        return alternativeCorrectId;
    }

    public void setAlternativeCorrectId(Long alternativeCorrectId) {
        this.alternativeCorrectId = alternativeCorrectId;
    }
}
