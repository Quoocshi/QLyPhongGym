package hahaha.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hahaha.enums.LoaiNhanVien;
import hahaha.model.NhanVien;

public interface NhanVienRepository extends JpaRepository<NhanVien,String> {
    
    @Query("SELECT n FROM NhanVien n JOIN Account a ON a.nhanVien.maNV = n.maNV WHERE a.isDeleted = 0")
    List<NhanVien> findAllActive();
    
    @Query("SELECT n FROM NhanVien n JOIN Account a ON a.nhanVien.maNV = n.maNV WHERE n.maNV = ?1 AND a.isDeleted = 0")
    Optional<NhanVien> findByIdActive(String maNV);
    
    @Query("SELECT n FROM NhanVien n JOIN Account a ON a.nhanVien.maNV = n.maNV WHERE a.isDeleted = 0")
    List<NhanVien> findAllNotDeleted();
    

}
