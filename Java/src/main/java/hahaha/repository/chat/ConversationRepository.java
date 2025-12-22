package hahaha.repository.chat;

import hahaha.enums.ConversationStatus;
import hahaha.model.Conversation;
import hahaha.model.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Modifying
    @Query("""
    UPDATE Conversation c
    SET c.nhanVien.maNV = :maNV,
        c.status = 'ASSIGNED'
    WHERE c.conversationId = :id
      AND c.nhanVien IS NULL
""")
    int assignConversation(@Param("id") Long id,
                           @Param("maNV") String maNV);

    Conversation findByConversationId(Long id);
    Optional<Conversation> findFirstByKhachHang_MaKHAndStatusInOrderByLastMessageAtDesc(
            String maKH,
            List<ConversationStatus> statuses
    );

}

