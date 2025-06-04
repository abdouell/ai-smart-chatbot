package com.chatbot.aifaqchatbot.controller;
import com.chatbot.aifaqchatbot.model.FaqItem;
import com.chatbot.aifaqchatbot.model.FaqResponse;
import com.chatbot.aifaqchatbot.repository.FaqItemRepository;
import com.chatbot.aifaqchatbot.service.ChatbotService;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/faq")
@CrossOrigin(value = "*")
public class FaqController {

    private final ChatbotService chatbotService;
    private final FaqItemRepository faqItemRepository;
    private final EmbeddingModel embeddingModel;

    public FaqController(ChatbotService chatbotService, FaqItemRepository faqItemRepository, EmbeddingModel embeddingModel) {
        this.chatbotService = chatbotService;
        this.faqItemRepository = faqItemRepository;
        this.embeddingModel = embeddingModel;
    }

    @GetMapping("/ask")
    public FaqResponse askQuestion(@RequestParam String question) {
        FaqResponse response = chatbotService.findAnswer(question);
        return response;
    }

    @GetMapping("/ask-vector")
    public String askQuestionUsingVector(@RequestParam String question) {
        return chatbotService.findAnswerUsingVector(question);
    }

    // CRUD operations for FaqItem

    @GetMapping
    public List<FaqItem> getAllFaqItems() {
        return faqItemRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaqItem> getFaqItemById(@PathVariable Long id) {
        return faqItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FaqItem createFaqItem(@RequestBody FaqItem faqItem) {
        // Generate embedding for the question
        float[] embedding = embeddingModel.embed(faqItem.getQuestion());
        try {
            // Convert embedding to JSON string
            faqItem.setEmbedding(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(embedding));
        } catch (Exception e) {
            throw new RuntimeException("Error generating embedding", e);
        }
        return faqItemRepository.save(faqItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FaqItem> updateFaqItem(@PathVariable Long id, @RequestBody FaqItem faqItemDetails) {
        return faqItemRepository.findById(id)
                .map(faqItem -> {
                    faqItem.setQuestion(faqItemDetails.getQuestion());
                    faqItem.setReponse(faqItemDetails.getReponse());

                    // Update embedding if question changed
                    if (!faqItem.getQuestion().equals(faqItemDetails.getQuestion())) {
                        float[] embedding = embeddingModel.embed(faqItemDetails.getQuestion());
                        try {
                            faqItem.setEmbedding(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(embedding));
                        } catch (Exception e) {
                            throw new RuntimeException("Error updating embedding", e);
                        }
                    }

                    FaqItem updatedFaqItem = faqItemRepository.save(faqItem);
                    return ResponseEntity.ok(updatedFaqItem);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaqItem(@PathVariable Long id) {
        return faqItemRepository.findById(id)
                .map(faqItem -> {
                    faqItemRepository.delete(faqItem);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
