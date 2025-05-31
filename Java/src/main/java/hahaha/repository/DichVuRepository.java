package hahaha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hahaha.model.DichVu;

public interface DichVuRepository extends JpaRepository<DichVu, String> {
    @Query("""
    SELECT d FROM DichVu d
    WHERE d.maDV NOT IN (
        SELECT ct.dichVu.maDV
        FROM ChiTietDangKyDichVu ct
        WHERE ct.hoaDon.khachHang.maKH = :maKH
        )
    """)
    List<DichVu> listDichVuKhachHangChuaDangKy(String maKH);
    
    @Query("""
    SELECT d FROM DichVu d
    WHERE d.boMon.maBM = :maBM 
    AND d.maDV NOT IN (
        SELECT ct.dichVu.maDV
        FROM ChiTietDangKyDichVu ct
        WHERE ct.hoaDon.khachHang.maKH = :maKH
        )
    """)
    List<DichVu> listDichVuTheoBoMonKhachHangChuaDangKy(String maBM, String maKH);
}
