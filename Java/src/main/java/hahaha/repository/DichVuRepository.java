package hahaha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    // Query mới: Filter theo bộ môn và thời hạn
    @Query("""
    SELECT d FROM DichVu d
    WHERE d.boMon.maBM = :maBM 
    AND d.maDV NOT IN (
        SELECT ct.dichVu.maDV
        FROM ChiTietDangKyDichVu ct
        WHERE ct.hoaDon.khachHang.maKH = :maKH
        )
    AND (
        :thoiHanFilter = '' OR :thoiHanFilter IS NULL OR
        (:thoiHanFilter = '7' AND d.thoiHan <= 7) OR
        (:thoiHanFilter = '30' AND d.thoiHan > 7 AND d.thoiHan <= 30) OR
        (:thoiHanFilter = '90' AND d.thoiHan > 30 AND d.thoiHan <= 90) OR
        (:thoiHanFilter = 'khac' AND d.thoiHan > 90)
    )
    """)
    List<DichVu> listDichVuTheoBoMonVaThoiHanKhachHangChuaDangKy(
        @Param("maBM") String maBM, 
        @Param("maKH") String maKH, 
        @Param("thoiHanFilter") String thoiHanFilter
    );
    
    // Method missing in controller
    List<DichVu> findByBoMon_MaBM(String maBM);
}
