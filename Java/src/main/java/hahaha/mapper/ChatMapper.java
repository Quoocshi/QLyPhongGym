package hahaha.mapper;

import hahaha.DTO.ChatMessageResponseDTO;
import hahaha.model.Message;

public class ChatMapper {

    private ChatMapper() {}

    public static ChatMessageResponseDTO toResponseDTO(
            Message message,
            String senderType,
            String senderName
    ) {
        ChatMessageResponseDTO dto = new ChatMessageResponseDTO();

        dto.setMessageId(message.getMessageId());
        dto.setConversationId(
                message.getConversation().getConversationId()
        );
        dto.setContent(message.getContent());
        dto.setCreatedAt(message.getCreatedAt());

        dto.setSenderType(senderType);
        dto.setSenderName(senderName);

        if (message.getSenderMaNV() != null) {
            dto.setSenderId(message.getSenderMaNV());
        } else {
            dto.setSenderId(message.getSenderMaKH());
        }

        return dto;
    }
}
