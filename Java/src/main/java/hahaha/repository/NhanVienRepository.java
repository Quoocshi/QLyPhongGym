package hahaha.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hahaha.model.NhanVien;

public interface NhanVienRepository extends JpaRepository<NhanVien,String> {
    // @Query("SELECT MAX(CAST(SUBSTRING(n.maNV, 3) AS int)) FROM NhanVien n WHERE n.maNV LIKE 'NV%'")
    // Integer findMaxMaNVNumber();

    // @Query("SELECT MAX(CAST(SUBSTRING(n.maNV, 3) AS int)) FROM NhanVien n WHERE n.maNV LIKE 'QL%'")
    // Integer findMaxMaQLNumber();

    // @Query("SELECT MAX(CAST(SUBSTRING(n.maNV, 3) AS int)) FROM NhanVien n WHERE n.maNV LIKE 'PT%'")
    // Integer findMaxMaPTNumber();
}
