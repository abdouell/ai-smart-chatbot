package com.chatbot.aifaqchatbot.controller;

import com.chatbot.aifaqchatbot.model.FeedbackData;
import com.chatbot.aifaqchatbot.model.Message;
import com.chatbot.aifaqchatbot.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/message")
@CrossOrigin(value = "*")

public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/feed-back")
    public void addFeedBack(@RequestBody FeedbackData feedBack) {
        messageService.addFeedBack(feedBack);
    }

    @GetMapping
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        return messageService.getMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/feedback")
    public List<Message> getMessagesWithFeedback() {
        return messageService.getMessagesWithFeedback();
    }
}
