package com.chatbot.aifaqchatbot;

import com.chatbot.aifaqchatbot.model.FaqItem;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        // File path (change according to your actual file location)
        File faqFile = new File("src/main/resources/faq.json");

        try {
            // Read the JSON into a list of FaqItem objects
            List<FaqItem> faqItems = objectMapper.readValue(
                    faqFile,
                    objectMapper
                            .getTypeFactory()
                            .constructCollectionType(List.class, FaqItem.class)
            );

            // Print all FAQ items
            for (FaqItem item : faqItems) {
                System.out.println("Question: " + item.getQuestion());
                System.out.println("Réponse: " + item.getReponse());
                System.out.println("------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
