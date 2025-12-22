package hahaha.controller.websocket;

import hahaha.model.CustomUserDetails;
import hahaha.model.NhanVien;

import hahaha.service.chat.ConversationService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;
    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/{id}/assign")
    public void assignConversation(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        conversationService.assignConversation(id, user.getAccountId());
    }

}

