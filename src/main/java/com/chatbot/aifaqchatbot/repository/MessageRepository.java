package com.chatbot.aifaqchatbot.repository;


import com.chatbot.aifaqchatbot.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
