package com.chatbot.aifaqchatbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "faq_vector_store")
public class FaqItem {
    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String reponse;

    @Column(columnDefinition = "TEXT")
    private String embedding;


    public FaqItem(String question, String reponse, String embedding) {
        this.question = question;
        this.reponse = reponse;
        this.embedding = embedding;
    }

    public FaqItem() {

    }

    public String getEmbedding() {
       return embedding;
    }

    public void setEmbedding(String embedding) throws JsonProcessingException {
        this.embedding = embedding;
    }

    // Getters and setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }
}
