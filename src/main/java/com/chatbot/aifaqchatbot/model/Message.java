package com.chatbot.aifaqchatbot.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String userQuestion;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String reponse;

    @Column(columnDefinition = "TEXT")
    private String faqSimilaires;

    private String feedback;

    private Boolean isFeedBackPositive;

    @Column(nullable = false)
    private boolean horsSujet;

    public Message(Long id,
                   String userQuestion,
                   LocalDateTime timestamp,
                   String reponse,
                   String faqSimilaires,
                   String feedback,
                   boolean horsSujet) {
        this.timestamp = timestamp;
        this.id = id;
        this.userQuestion = userQuestion;
        this.reponse = reponse;
        this.faqSimilaires = faqSimilaires;
        this.feedback = feedback;
        this.horsSujet = horsSujet;
    }

    public Message() {

    }

    public Long getId() {
        return id;
    }

    public String getUserQuestion() {
        return userQuestion;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getReponse() {
        return reponse;
    }

    public String getFaqSimilaires() {
        return faqSimilaires;
    }

    public String getFeedback() {
        return feedback;
    }

    public boolean isHorsSujet() {
        return horsSujet;
    }

    public void setUserQuestion(String userQuestion) {
        this.userQuestion = userQuestion;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public void setFaqSimilaires(String faqSimilaires) {
        this.faqSimilaires = faqSimilaires;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setHorsSujet(boolean horsSujet) {
        this.horsSujet = horsSujet;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFeedBackPositive() {
        return isFeedBackPositive;
    }

    public void setFeedBackPositive(Boolean feedBackPositive) {
        isFeedBackPositive = feedBackPositive;
    }
}
