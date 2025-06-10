package hahaha.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hahaha.enums.LoaiNhanVien;
import hahaha.model.NhanVien;

public interface NhanVienRepository extends JpaRepository<NhanVien,String> {
    
    @Query("SELECT n FROM NhanVien n WHERE n.isDeleted = 0")
    List<NhanVien> findAllActive();
    
    @Query("SELECT n FROM NhanVien n WHERE n.maNV = ?1 AND n.isDeleted = 0")
    Optional<NhanVien> findByIdActive(String maNV);
    
    @Query("SELECT n FROM NhanVien n WHERE n.isDeleted = 0")
    List<NhanVien> findAllNotDeleted();
    
    @Query("""
        SELECT n FROM NhanVien n WHERE n.isDeleted = 0 
        AND (
            :keyword IS NULL OR :keyword = '' OR
            LOWER(TRIM(n.tenNV)) LIKE LOWER(CONCAT('%', TRIM(:keyword), '%')) OR
            LOWER(TRIM(n.email)) LIKE LOWER(CONCAT('%', TRIM(:keyword), '%')) OR
            LOWER(TRIM(n.maNV)) LIKE LOWER(CONCAT('%', TRIM(:keyword), '%'))
        )
        AND (:loaiNV IS NULL OR :loaiNV = '' OR n.loaiNV = :loaiNVEnum)
    """)
    List<NhanVien> searchNhanVien(@Param("keyword") String keyword, @Param("loaiNV") String loaiNV, @Param("loaiNVEnum") LoaiNhanVien loaiNVEnum);
    
    // Method để test tìm kiếm đơn giản
    @Query("SELECT n FROM NhanVien n WHERE n.isDeleted = 0 AND LOWER(n.tenNV) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<NhanVien> findByTenNVContainingIgnoreCase(@Param("keyword") String keyword);
    
    // Method để debug - lấy tất cả tên nhân viên
    @Query("SELECT n FROM NhanVien n WHERE n.isDeleted = 0")
    List<NhanVien> getAllForDebug();
    
    // Method tìm kiếm không dấu - workaround cho vấn đề encoding
    @Query(value = """
        SELECT * FROM NHANVIEN n WHERE n.ISDELETED = 0 
        AND (
            :keyword IS NULL OR :keyword = '' OR
            UPPER(TRANSLATE(n.TENNV, 
                'àáảãạăắằẳẵặâấầẩẫậèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵđ',
                'AAAAAAAAAAAAAAAEEEEEEEEEEEIIIIIOOOOOOOOOOOOOOOOUUUUUUUUUUUYYYYYD'
            )) LIKE UPPER(TRANSLATE(:keyword,
                'àáảãạăắằẳẵặâấầẩẫậèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵđ',
                'AAAAAAAAAAAAAAAEEEEEEEEEEEIIIIIOOOOOOOOOOOOOOOOUUUUUUUUUUUYYYYYD'
            ) || '%') OR
            UPPER(n.EMAIL) LIKE UPPER(:keyword || '%') OR
            UPPER(n.MANV) LIKE UPPER(:keyword || '%')
        )
        AND (:loaiNV IS NULL OR :loaiNV = '' OR n.LOAINV = :loaiNV)
    """, nativeQuery = true)
    List<NhanVien> searchNhanVienUnaccented(@Param("keyword") String keyword, @Param("loaiNV") String loaiNV);
}
