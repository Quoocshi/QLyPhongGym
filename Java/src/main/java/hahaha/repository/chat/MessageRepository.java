package hahaha.repository.chat;


import hahaha.model.Conversation;
import hahaha.model.Message;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Load lịch sử chat theo conversation
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);
}