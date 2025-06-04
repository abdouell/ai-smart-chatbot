package com.chatbot.aifaqchatbot.service;

import com.chatbot.aifaqchatbot.model.FaqItem;
import com.chatbot.aifaqchatbot.repository.FaqItemRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@BrowserCallable
@AnonymousAllowed
public class FaqSearchService {
    private EmbeddingModel embeddingModel;
    private FaqItemRepository faqItemRepository;

    public FaqSearchService(EmbeddingModel embeddingModel,
                            FaqItemRepository faqItemRepository) {
        this.embeddingModel = embeddingModel;
        this.faqItemRepository = faqItemRepository;
    }


    public List<FaqItem> findBestMatchesFaq(String userQuestion) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Double> questionEmbedding = convertToList(embeddingModel.embed(userQuestion));
        List<FaqItem> faqItems = faqItemRepository.findAll();

        List<FaqItem> bestMatches = faqItems.stream()
                .map(faq -> {
                    try {
                        return new AbstractMap.SimpleEntry<>(faq, cosineSimilarity(questionEmbedding, convertToList(objectMapper.readValue(faq.getEmbedding(), float[].class))));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(entry -> entry.getValue() > 0.8)
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(AbstractMap.SimpleEntry::getKey)
                .collect(Collectors.toList());

        return bestMatches;
    }

    private List<Double> convertToList(float[] array) {
        List<Double> list = new ArrayList<>();
        for (float val : array) {
            list.add((double) val);
        }
        return list;
    }

    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        double dotProduct = 0.0, normV1 = 0.0, normV2 = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            normV1 += Math.pow(v1.get(i), 2);
            normV2 += Math.pow(v2.get(i), 2);
        }
        return dotProduct / (Math.sqrt(normV1) * Math.sqrt(normV2));
    }


}
