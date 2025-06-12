package hahaha.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hahaha.model.DoanhThu;
import hahaha.model.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    @Query("SELECT MAX(CAST(SUBSTRING(h.maHD, 3) AS int)) FROM HoaDon h WHERE h.maHD LIKE 'HD%'")
    Integer findMaxMaHoaDonNumber();

   @Query("""
    SELECT new hahaha.model.DoanhThu(CAST(h.ngayTT AS date), SUM(h.tongTien))
    FROM HoaDon h
    WHERE h.trangThai = hahaha.enums.TrangThaiHoaDon.DaThanhToan
    GROUP BY CAST(h.ngayTT AS date)
    ORDER BY CAST(h.ngayTT AS date)
    """)
    List<DoanhThu> thongKeDoanhThuTheoNgay();



    
}
