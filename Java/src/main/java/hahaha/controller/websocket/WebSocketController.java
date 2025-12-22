//package hahaha.controller.websocket;
//
//import hahaha.DTO.ChatMessageRequestDTO;
//import hahaha.DTO.ChatMessageResponseDTO;
//import hahaha.model.Conversation;
//import hahaha.model.CustomUserDetails;
//import hahaha.repository.AccountRepository;
//import hahaha.service.chat.ConversationService;
//import hahaha.service.chat.MessageService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class WebSocketController {
//
//    @Autowired
//    private MessageService messageService;
//
//    @Autowired
//    private ConversationService conversationService;
//
//    @MessageMapping("/chat.send")
//    public ChatMessageResponseDTO sendMessage(
//            @Payload ChatMessageRequestDTO dto,
//            Authentication auth) {
//
//        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
//
//        Long accountId = user.getAccountId();
//
//        // ✅ validate bằng accountId
//        Conversation conversation = conversationService
//                .validateEmployeePermission(dto.getConversationId(), accountId);
//
//        // ✅ gửi message cũng bằng accountId
//        return messageService
//                .saveEmployeeMessage(conversation, accountId, dto.getContent());
//    }
//}
