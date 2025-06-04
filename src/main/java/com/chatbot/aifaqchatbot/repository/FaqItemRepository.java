package com.chatbot.aifaqchatbot.repository;


import com.chatbot.aifaqchatbot.model.FaqItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqItemRepository extends JpaRepository<FaqItem, Long> {
}
