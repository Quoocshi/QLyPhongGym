package hahaha.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hahaha.model.NhanVien;

public interface NhanVienRepository extends JpaRepository<NhanVien,String> {
    
    @Query("SELECT n FROM NhanVien n JOIN Account a ON a.nhanVien.maNV = n.maNV WHERE a.isDeleted = 0")
    List<NhanVien> findAllActive();
    
    @Query("SELECT n FROM NhanVien n JOIN Account a ON a.nhanVien.maNV = n.maNV WHERE n.maNV = ?1 AND a.isDeleted = 0")
    Optional<NhanVien> findByIdActive(String maNV);
    
    @Query("SELECT n FROM NhanVien n JOIN Account a ON a.nhanVien.maNV = n.maNV WHERE a.isDeleted = 0")
    List<NhanVien> findAllNotDeleted();
    
    @Query("""
        SELECT acc.nhanVien FROM Account acc
        WHERE acc.isDeleted = 0
        AND acc.nhanVien IS NOT NULL
        AND (
                LOWER(acc.nhanVien.maNV) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(acc.nhanVien.tenNV) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(acc.nhanVien.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(CAST(acc.nhanVien.loaiNV AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
    """)
    List<NhanVien> searchActiveEmployeesByKeyword(@Param("keyword") String keyword);

}
