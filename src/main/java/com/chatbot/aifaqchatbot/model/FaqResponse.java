package com.chatbot.aifaqchatbot.model;

import java.util.List;

public class FaqResponse {

    private String response;

    private List<FaqItem> nearFaqItems;

    private Long messageId;


    public FaqResponse(String response, List<FaqItem> nearFaqItems, Long messageId) {
        this.response = response;
        this.nearFaqItems = nearFaqItems;
        this.messageId = messageId;
    }

    public FaqResponse() {
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<FaqItem> getNearFaqItems() {
        return nearFaqItems;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public void setNearFaqItems(List<FaqItem> nearFaqItems) {
        this.nearFaqItems = nearFaqItems;
    }
}
