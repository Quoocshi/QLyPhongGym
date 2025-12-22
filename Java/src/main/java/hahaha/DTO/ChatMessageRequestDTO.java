package hahaha.DTO;

import lombok.Data;

@Data
public class ChatMessageRequestDTO {
    private Long conversationId;
    private String content;
}

