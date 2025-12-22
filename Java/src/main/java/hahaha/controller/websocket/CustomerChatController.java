package hahaha.controller.websocket;

import hahaha.DTO.ChatMessageRequestDTO;
import hahaha.DTO.ChatMessageResponseDTO;
import hahaha.model.CustomUserDetails;
import hahaha.service.chat.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class CustomerChatController {

    @Autowired
    private MessageService messageService;

    @MessageMapping("/chat.customer.send")
    @PreAuthorize("hasRole('USER')")
    public ChatMessageResponseDTO sendCustomerMessage(
            @Payload ChatMessageRequestDTO dto,
            Authentication auth
    ) {
        System.out.println(">>> CUSTOMER SEND MESSAGE");

        CustomUserDetails user =
                (CustomUserDetails) auth.getPrincipal();

        Long accountId = user.getAccountId();

        return messageService.saveCustomerMessage(
                dto.getConversationId(),
                accountId,
                dto.getContent()
        );
    }
}
