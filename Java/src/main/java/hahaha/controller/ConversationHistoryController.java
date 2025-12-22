package hahaha.controller;

import hahaha.DTO.ConversationDTO;
import hahaha.model.Conversation;
import hahaha.model.CustomUserDetails;
import hahaha.service.chat.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conversation-history")
@RequiredArgsConstructor
public class ConversationHistoryController {
    @Autowired
    private ConversationService conversationService;
    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public List<ConversationDTO> findAllConversationsForStaff(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        // Lấy maNV từ authenticated user
        String currentMaNV = user.getNhanVien().getMaNV();

        return conversationService.findAllConversations()
                .stream()
                .map(conv -> mapToDTO(conv, currentMaNV))
                .toList();
    }

    private ConversationDTO mapToDTO(Conversation c, String currentMaNV) {

        boolean assignedToMe =
                c.getNhanVien() != null &&
                        c.getNhanVien().getMaNV().equals(currentMaNV);

        return new ConversationDTO(
                c.getConversationId().toString(),
                c.getKhachHang().getHoTen(),
                c.getStatus(),
                assignedToMe,
                c.getLastMessageAt()
        );
    }

}
