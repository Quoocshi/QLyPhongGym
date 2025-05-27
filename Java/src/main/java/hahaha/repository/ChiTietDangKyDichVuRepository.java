package hahaha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hahaha.model.ChiTietDangKyDichVu;

public interface ChiTietDangKyDichVuRepository extends JpaRepository<ChiTietDangKyDichVu, String> {
    @Query("SELECT MAX(CAST(SUBSTRING(ct.maCTDK, 3) AS int)) FROM ChiTietDangKyDichVu ct WHERE ct.maCTDK LIKE 'CT%'")
    Integer findMaxChiTietDangKyDichVuNumber();
}

