package com.chatbot.aifaqchatbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedbackData {

    private Long messageId;

    private Boolean isPositive;

    private String detailedFeedback;

    public FeedbackData(Boolean isPositive, String detailedFeedback) {
        this.isPositive = isPositive;
        this.detailedFeedback = detailedFeedback;
    }

    public Boolean getPositive() {
        return isPositive;
    }

    public String getDetailedFeedback() {
        return detailedFeedback;
    }

    public void setPositive(Boolean positive) {
        isPositive = positive;
    }

    public void setDetailedFeedback(String detailedFeedback) {
        this.detailedFeedback = detailedFeedback;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
}
