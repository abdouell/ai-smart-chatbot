package com.chatbot.aifaqchatbot.config;


import com.chatbot.aifaqchatbot.model.FaqItem;
import com.chatbot.aifaqchatbot.model.FaqItemDto;
import com.chatbot.aifaqchatbot.repository.FaqItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FaqDataLoader {
    @Value("classpath:/pdf/faq.pdf")
    private Resource pdfResource;
    private JdbcClient jdbcClient;
    private VectorStore faqVectorStore;
    private FaqItemRepository faqItemRepository;
    private final EmbeddingModel embeddingModel;

    public FaqDataLoader(JdbcClient jdbcClient,
                         VectorStore faqVectorStore,
                         FaqItemRepository faqItemRepository, EmbeddingModel embeddingModel) {
        this.jdbcClient = jdbcClient;
        this.faqVectorStore = faqVectorStore;
        this.faqItemRepository = faqItemRepository;
        this.embeddingModel = embeddingModel;
    }


    @PostConstruct
    public void initStore() {
        Integer count = jdbcClient.sql("select count(*) from vector_store")
                .query(Integer.class).single();
        if(count == 0) {
            PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(pdfResource);
            List<Document> documents = pagePdfDocumentReader.read();
            TextSplitter textSplitter = new TokenTextSplitter(true);
            List<Document> chuncks = textSplitter.split(documents);
            faqVectorStore.add(chuncks);
            this.saveFaqData();
        }
    }

    private List<FaqItemDto> saveFaqData() {
        int count = faqItemRepository.findAll().size();
        if(count == 0) {
            ObjectMapper objectMapper = new ObjectMapper();

            // File path (change according to your actual file location)
            File faqFile = new File("src/main/resources/faq.json");

            try {
                // Read the JSON into a list of FaqItem objects
                List<FaqItemDto> faqItems = objectMapper.readValue(
                        faqFile,
                        objectMapper
                                .getTypeFactory()
                                .constructCollectionType(List.class, FaqItemDto.class)
                );

                // Print all FAQ items
                for (FaqItemDto item : faqItems) {
                    float[] questionEmbedding = embeddingModel.embed(item.getQuestion());
                    item.setEmbedding(objectMapper.writeValueAsString(questionEmbedding));

                    faqItemRepository.save(
                            new FaqItem(
                                    item.getQuestion(),
                                    item.getReponse(),
                                    item.getEmbedding()
                            )
                    );

                    System.out.println("Question: " + item.getQuestion());
                    System.out.println("Réponse: " + item.getReponse());
                    System.out.println("------------------------------------");
                }
                return faqItems;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

}
