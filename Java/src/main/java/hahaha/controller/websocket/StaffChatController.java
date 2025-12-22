package hahaha.controller.websocket;

import hahaha.DTO.ChatMessageRequestDTO;
import hahaha.DTO.ChatMessageResponseDTO;
import hahaha.model.Conversation;
import hahaha.model.CustomUserDetails;
import hahaha.service.chat.ConversationService;
import hahaha.service.chat.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class StaffChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConversationService conversationService;

    @MessageMapping("/chat.staff.send")
    @PreAuthorize("hasRole('STAFF')")
    public ChatMessageResponseDTO sendStaffMessage(
            @Payload ChatMessageRequestDTO dto,
            Authentication auth
    ) {
        CustomUserDetails user =
                (CustomUserDetails) auth.getPrincipal();

        Long accountId = user.getAccountId();

        Conversation conversation =
                conversationService.validateEmployeePermission(
                        dto.getConversationId(),
                        accountId
                );

        return messageService.saveEmployeeMessage(
                conversation,
                accountId,
                dto.getContent()
        );
    }
}

