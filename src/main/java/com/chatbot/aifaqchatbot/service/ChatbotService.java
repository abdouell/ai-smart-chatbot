package com.chatbot.aifaqchatbot.service;

import com.chatbot.aifaqchatbot.model.FaqItem;
import com.chatbot.aifaqchatbot.model.FaqResponse;
import com.chatbot.aifaqchatbot.model.Message;
import com.chatbot.aifaqchatbot.repository.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@BrowserCallable
@AnonymousAllowed
public class ChatbotService {
    private final OpenAiService openAiService;
    private VectorStore vectorStore;
    private FaqSearchService faqSearchService;
    private QuestionClassifierService questionClassifierService;
    private MessageRepository messageRepository;


    public ChatbotService(OpenAiService openAiService,
                          VectorStore vectorStore,
                          FaqSearchService faqSearchService,
                          QuestionClassifierService questionClassifierService,
                          MessageRepository messageRepository) {
        this.openAiService = openAiService;
        this.vectorStore = vectorStore;
        this.faqSearchService = faqSearchService;
        this.questionClassifierService = questionClassifierService;
        this.messageRepository = messageRepository;
    }

    public FaqResponse findAnswer(String userQuestion) {

        FaqResponse response = questionClassifierService.findAnswer(userQuestion);
        if (response != null && !"FAQ".equals(response.getResponse())) {
            Long messageId = saveMessage(userQuestion, response, true);
            response.setMessageId(messageId);
            return response;
        }

        List<FaqItem> bestMatches = faqSearchService.findBestMatchesFaq(userQuestion);

        List<String> faqContext = bestMatches.stream()
                .map(entry -> "Q: " + entry.getQuestion() + "\nA: " + entry.getReponse())
                .collect(Collectors.toList());

        response = openAiService.generateResponse(userQuestion, faqContext);

        Long messageId = saveMessage(userQuestion, response, false);
        response.setMessageId(messageId);

        return response;
    }

    private Long saveMessage(String userQuestion, FaqResponse response, boolean horsSujet) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonFaq = mapper.writeValueAsString(response.getNearFaqItems());
            Message message = new Message(
                    null,
                    userQuestion,
                    LocalDateTime.now(),
                    response.getResponse(),
                    jsonFaq,
                    null,
                    horsSujet
            );
            return messageRepository.save(message).getId();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String findAnswerUsingVector(String userQuestion) {
        List<Document> documents = this.vectorStore.similaritySearch(userQuestion);
        List<String> context = documents.stream().map(Document::getContent).toList();
        return openAiService.generateResponse2(userQuestion, context);
    }

}
