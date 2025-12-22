package hahaha.controller;

import hahaha.DTO.ChatMessageResponseDTO;
import hahaha.enums.ConversationStatus;
import hahaha.model.Conversation;
import hahaha.model.CustomUserDetails;
import hahaha.repository.AccountRepository;
import hahaha.repository.chat.ConversationRepository;
import hahaha.service.chat.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final MessageService messageService;

    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/{id}/messages")
    public List<ChatMessageResponseDTO> loadMessages(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return messageService.loadConversationMessages(
                id,
                user.getAccountId()
        );
    }

    @GetMapping("/my-conversation")
    public Map<String, Object> getMyConversation(@AuthenticationPrincipal CustomUserDetails user) {
        String maKH = accountRepository.findByAccountId(user.getAccountId())
                .getKhachHang().getMaKH();

        Optional<Conversation> conv = conversationRepository
                .findFirstByKhachHang_MaKHAndStatusInOrderByLastMessageAtDesc(
                        maKH,
                        List.of(ConversationStatus.NEW, ConversationStatus.ASSIGNED)
                );

        if (conv.isPresent()) {
            return Map.of("conversationId", conv.get().getConversationId());
        }
        return null; // or return empty map
    }
}

