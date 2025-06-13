package hahaha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hahaha.model.ChiTietDangKyDichVu;

public interface ChiTietDangKyDichVuRepository extends JpaRepository<ChiTietDangKyDichVu, String> {
    @Query("SELECT MAX(CAST(SUBSTRING(ct.maCTDK, 3) AS int)) FROM ChiTietDangKyDichVu ct WHERE ct.maCTDK LIKE 'CT%'")
    Integer findMaxChiTietDangKyDichVuNumber();
    
    @Query("SELECT ct FROM ChiTietDangKyDichVu ct WHERE ct.hoaDon.khachHang.maKH = :maKH ORDER BY ct.ngayBD DESC")
    List<ChiTietDangKyDichVu> findByHoaDon_KhachHang_MaKHOrderByNgayBDDesc(String maKH);
    
    @Query("SELECT ct FROM ChiTietDangKyDichVu ct WHERE ct.hoaDon.khachHang.maKH = :maKH ORDER BY ct.ngayBD DESC")
    List<ChiTietDangKyDichVu> findByKhachHang_MaKH(String maKH);
    
    // Query lấy chỉ dịch vụ từ hóa đơn đã thanh toán
    @Query("SELECT ct FROM ChiTietDangKyDichVu ct WHERE ct.hoaDon.khachHang.maKH = :maKH AND ct.hoaDon.trangThai = 'DaThanhToan' ORDER BY ct.ngayBD DESC")
    List<ChiTietDangKyDichVu> findByKhachHang_MaKH_DaThanhToan(String maKH);
    
    // Query tìm khách hàng đã đăng ký PT với trainer cụ thể
    @Query("SELECT ct FROM ChiTietDangKyDichVu ct " +
           "WHERE ct.nhanVien.maNV = :maNV " +
           "AND ct.dichVu.loaiDV = 'PT' " +
           "AND ct.hoaDon.trangThai = 'DaThanhToan' " +
           "AND ct.ngayKT >= CURRENT_DATE " +
           "ORDER BY ct.ngayBD DESC")
    List<ChiTietDangKyDichVu> findPTCustomersByTrainer(@Param("maNV") String maNV);
    
    // Query lấy thông tin PT của khách hàng cụ thể
    @Query("SELECT ct FROM ChiTietDangKyDichVu ct " +
           "WHERE ct.hoaDon.khachHang.maKH = :maKH " +
           "AND ct.dichVu.loaiDV = 'PT' " +
           "AND ct.hoaDon.trangThai = 'DaThanhToan' " +
           "AND ct.ngayKT >= CURRENT_DATE " +
           "ORDER BY ct.ngayBD DESC")
    List<ChiTietDangKyDichVu> findPTCustomersByCustomer(@Param("maKH") String maKH);
}

