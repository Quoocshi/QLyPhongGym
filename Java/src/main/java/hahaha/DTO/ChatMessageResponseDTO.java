package hahaha.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponseDTO {
    private Long messageId;
    private Long conversationId;
    private String senderType; // KHACH_HANG | NHAN_VIEN
    private String senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;
}
