package hahaha.service.chat;

import hahaha.DTO.ChatMessageResponseDTO;
import hahaha.enums.ConversationStatus;
import hahaha.mapper.ChatMapper;
import hahaha.model.Account;
import hahaha.model.Conversation;
import hahaha.model.Message;
import hahaha.model.NhanVien;
import hahaha.repository.AccountRepository;
import hahaha.repository.NhanVienRepository;
import hahaha.repository.chat.ConversationRepository;
import hahaha.repository.chat.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;
    private final NhanVienRepository nhanVienRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatMessageResponseDTO saveEmployeeMessage(
            Conversation conversation,
            Long accountId,
            String content
    ) {
        // Kiểm tra conversation đã được assign và đang active
        if (conversation.getNhanVien().getMaNV() == null) {
            throw new IllegalStateException("Conversation chưa được assign");
        }

        String maNV = accountRepository
                .findByAccountId(accountId)
                .getNhanVien()
                .getMaNV();

        // Đảm bảo chỉ nhân viên được assign mới gửi được
        if (!conversation.getNhanVien().getMaNV().equals(maNV)) {
            throw new AccessDeniedException("Bạn không được phép nhắn tin trong hội thoại này");
        }

        Message msg = saveMessageCore(
                conversation,
                maNV,
                null,
                content
        );

        // Cập nhật trạng thái hội thoại
        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        String tenNV = nhanVienRepository
                .findById(maNV)
                .map(NhanVien::getTenNV)
                .orElse("Staff");

        ChatMessageResponseDTO response = ChatMapper.toResponseDTO(
                msg,
                "STAFF",
                tenNV
        );

        // Broadcast đến khách hàng
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversation.getConversationId(),
                response
        );

        return response;
    }

    @Transactional
    public ChatMessageResponseDTO saveCustomerMessage(
            Long conversationId,
            Long accountId,
            String content
    ) {
        String maKH = accountRepository
                .findByAccountId(accountId)
                .getKhachHang()
                .getMaKH();

        Conversation conversation;

        if (conversationId == null) {
            //tìm conversation đang active trước
            conversation = conversationRepository
                    .findFirstByKhachHang_MaKHAndStatusInOrderByLastMessageAtDesc(
                            maKH,
                            List.of(
                                    ConversationStatus.NEW,
                                    ConversationStatus.ASSIGNED
                            )
                    )
                    .orElseGet(() -> createNewConversation(maKH));
        } else {
            conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new IllegalArgumentException("Conversation không tồn tại"));

            if (!conversation.getKhachHang().getMaKH().equals(maKH)) {
                throw new AccessDeniedException("Không phải cuộc hội thoại của bạn");
            }
        }

        Message msg = saveMessageCore(
                conversation,
                null,
                maKH,
                content
        );

        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        ChatMessageResponseDTO response = ChatMapper.toResponseDTO(
                msg,
                "CUSTOMER",
                conversation.getKhachHang().getHoTen()
        );

        // gửi cho KH + NV
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversation.getConversationId(),
                response
        );

        messagingTemplate.convertAndSend(
                "/topic/staff/new-messages",
                response
        );

        return response;
    }


    private Conversation createNewConversation(String maKH) {
        Conversation conversation = new Conversation();
        conversation.setKhachHang(
                accountRepository.findByKhachHang_MaKH(maKH).getKhachHang()
        );
        conversation.setStatus(ConversationStatus.NEW);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setLastMessageAt(LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    private Message saveMessageCore(
            Conversation conversation,
            String senderMaNV,
            String senderMaKH,
            String content
    ) {
        Message msg = new Message();
        msg.setConversation(conversation);
        msg.setSenderMaNV(senderMaNV);
        msg.setSenderMaKH(senderMaKH);
        msg.setContent(content);
        msg.setCreatedAt(LocalDateTime.now());

        return messageRepository.save(msg);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponseDTO> loadConversationMessages(
            Long conversationId,
            Long accountId
    ) {
        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation không tồn tại"));

        Account account = accountRepository.findByAccountId(accountId);

        // Kiểm tra quyền truy cập
        if (account.getKhachHang() != null) {
            String maKH = account.getKhachHang().getMaKH();
            if (!conversation.getKhachHang().getMaKH().equals(maKH)) {
                throw new AccessDeniedException("Không phải cuộc hội thoại của bạn");
            }
        }

        if (account.getNhanVien() != null) {
            String maNV = account.getNhanVien().getMaNV();
            if (conversation.getNhanVien() == null ||
                    !conversation.getNhanVien().getMaNV().equals(maNV)) {
                throw new AccessDeniedException("Bạn chưa được assign vào cuộc hội thoại này");
            }
        }

        return messageRepository
                .findByConversationOrderByCreatedAtAsc(conversation)
                .stream()
                .map(this::mapMessageToDTO)
                .toList();
    }

    private ChatMessageResponseDTO mapMessageToDTO(Message message) {
        String senderType;
        String senderName;
        String senderId;

        if (message.getSenderMaNV() != null) {
            senderType = "STAFF";
            senderId = message.getSenderMaNV();
            senderName = nhanVienRepository
                    .findById(senderId)
                    .map(NhanVien::getTenNV)
                    .orElse("Staff");
        } else {
            senderType = "CUSTOMER";
            senderId = message.getSenderMaKH();
            senderName = message.getConversation()
                    .getKhachHang()
                    .getHoTen();
        }

        return ChatMapper.toResponseDTO(
                message,
                senderType,
                senderName
        );
    }
}