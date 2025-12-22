package hahaha.service.chat;

import hahaha.model.Conversation;
import hahaha.model.NhanVien;
import hahaha.repository.AccountRepository;
import hahaha.repository.chat.ConversationRepository;
import hahaha.repository.chat.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Transactional

    public void assignConversation(Long conversationId, Long accountId) {

        String maNV = accountRepository.findByAccountId(accountId).getNhanVien().getMaNV();

        int updated = conversationRepository.assignConversation(conversationId, maNV);

        if (updated == 0) {
            throw new IllegalStateException("Conversation already assigned");
        }
    }

    public Conversation validateEmployeePermission(
            Long conversationId, Long accountId) {

        String maNV = accountRepository.findByAccountId(accountId).getNhanVien().getMaNV();

        Conversation c = conversationRepository.findById(conversationId)
                .orElseThrow();

        if (c.getNhanVien().getMaNV() == null ||
                !c.getNhanVien().getMaNV().equals(maNV)) {
            throw new AccessDeniedException("No permission");
        }

        return c;
    }
    public List<Conversation> findAllConversations() {
        return conversationRepository.findAll();
    }

}
