package com.chatbot.aifaqchatbot.service;

import com.chatbot.aifaqchatbot.model.FeedbackData;
import com.chatbot.aifaqchatbot.model.Message;
import com.chatbot.aifaqchatbot.repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void addFeedBack(FeedbackData feedback) {
        Message message = messageRepository.findById(feedback.getMessageId()).orElseThrow();
        message.setFeedback(feedback.getDetailedFeedback());
        message.setFeedBackPositive(feedback.getPositive());
        messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    public List<Message> getMessagesWithFeedback() {
        return messageRepository.findAll().stream()
                .filter(message -> message.getFeedBackPositive() != null)
                .toList();
    }
}
