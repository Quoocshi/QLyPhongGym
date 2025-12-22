package hahaha.DTO;

import hahaha.enums.ConversationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ConversationDTO {
    private String conversationId;
    private String tenKH;
    private ConversationStatus status;
    private boolean assignedToMe;
    private LocalDateTime lastMessageAt;
}
