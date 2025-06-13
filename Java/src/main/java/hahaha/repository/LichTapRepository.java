package hahaha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hahaha.model.LichTap;

public interface LichTapRepository extends JpaRepository<LichTap, String> {
    
    // Tìm lịch tập theo mã lớp
    List<LichTap> findByLop_MaLop(String maLop);
    
    // Tìm lịch tập theo mã lớp và loại lịch
    List<LichTap> findByLop_MaLopAndLoaiLich(String maLop, String loaiLich);
    
    // Lấy các ca tập duy nhất cho một lớp
    @Query("SELECT DISTINCT lt.caTap FROM LichTap lt WHERE lt.lop.maLop = :maLop AND lt.loaiLich = 'Lop'")
    List<hahaha.model.CaTap> findDistinctCaTapByMaLop(@Param("maLop") String maLop);
    
    // Tìm lịch tập theo mã khách hàng
    List<LichTap> findByKhachHang_MaKH(String maKH);
    
    // Tìm lịch tập theo mã khách hàng và loại lịch
    List<LichTap> findByKhachHang_MaKHAndLoaiLich(String maKH, String loaiLich);
    
    // Lấy lịch tập PT của khách hàng
    @Query("SELECT lt FROM LichTap lt WHERE lt.khachHang.maKH = :maKH AND lt.loaiLich = 'PT'")
    List<LichTap> findPTScheduleByKhachHang(@Param("maKH") String maKH);
    
    // Lấy lịch lớp mà khách hàng đã đăng ký
    @Query("SELECT lt FROM LichTap lt WHERE lt.loaiLich = 'Lop' AND lt.lop.maLop IN " +
           "(SELECT ctdk.lop.maLop FROM ChiTietDangKyDichVu ctdk " +
           "WHERE ctdk.hoaDon.khachHang.maKH = :maKH " +
           "AND ctdk.lop IS NOT NULL " +
           "AND ctdk.hoaDon.trangThai = 'DaThanhToan')")
    List<LichTap> findClassScheduleByKhachHang(@Param("maKH") String maKH);
    
    // Tìm lịch PT theo trainer
    @Query("SELECT lt FROM LichTap lt WHERE lt.nhanVien.maNV = :maNV AND lt.loaiLich = 'PT' ORDER BY lt.maLT")
    List<LichTap> findPTScheduleByTrainer(@Param("maNV") String maNV);
    
    // Kiểm tra xung đột lịch
    @Query("SELECT COUNT(lt) FROM LichTap lt WHERE " +
           "lt.nhanVien.maNV = :maNV AND lt.caTap.maCa = :caTap AND lt.thu = :thu " +
           "AND lt.trangThai != 'Huy'")
    Long countConflictingSchedules(@Param("maNV") String maNV, @Param("caTap") String caTap, @Param("thu") String thu);
    
    // Kiểm tra xung đột lịch cho ngày cụ thể
    @Query("SELECT COUNT(lt) FROM LichTap lt WHERE " +
           "lt.nhanVien.maNV = :maNV AND lt.caTap.maCa = :caTap AND lt.thu = :ngayTap " +
           "AND lt.trangThai != 'Huy'")
    Long countConflictingSchedulesForDate(@Param("maNV") String maNV, @Param("caTap") String caTap, @Param("ngayTap") String ngayTap);
    
    // Generate next MaLT
    @Query("SELECT MAX(CAST(SUBSTRING(lt.maLT, 3) AS int)) FROM LichTap lt WHERE lt.maLT LIKE 'LT%'")
    Integer findMaxLichTapNumber();
    
    // Get last MaLT for debugging
    @Query("SELECT lt.maLT FROM LichTap lt ORDER BY lt.maLT DESC LIMIT 1")
    String findLastMaLT();
} 