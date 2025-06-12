package hahaha.service;

import java.util.List;
import hahaha.model.ChiTietDangKyDichVu;
import hahaha.model.LichTap;

public interface LichTapService {
    
    /**
     * Lấy tất cả lịch tập của khách hàng
     * @param maKH Mã khách hàng
     * @return Danh sách lịch tập
     */
    List<LichTap> getLichTapByKhachHang(String maKH);
    
    /**
     * Lấy lịch tập theo loại (PT hoặc Lop)
     * @param maKH Mã khách hàng
     * @param loaiLich Loại lịch (PT/Lop)
     * @return Danh sách lịch tập
     */
    List<LichTap> getLichTapByKhachHangAndLoai(String maKH, String loaiLich);
    
    /**
     * Lấy tất cả lịch tập của khách hàng (bao gồm cả lớp đã đăng ký)
     * @param maKH Mã khách hàng
     * @return Danh sách lịch tập đầy đủ
     */
    List<LichTap> getAllLichTapByKhachHang(String maKH);
    
    /**
     * Lấy danh sách khách hàng đã đăng ký PT với trainer
     * @param maNV Mã nhân viên trainer
     * @return Danh sách chi tiết đăng ký PT
     */
    List<ChiTietDangKyDichVu> getPTCustomersByTrainer(String maNV);
    
    /**
     * Lấy lịch PT của trainer
     * @param maNV Mã nhân viên trainer
     * @return Danh sách lịch tập PT
     */
    List<LichTap> getPTScheduleByTrainer(String maNV);
    
    /**
     * Tạo lịch tập PT mới
     * @param maNV Mã trainer
     * @param maKH Mã khách hàng
     * @param thu Thứ trong tuần
     * @param caTap Mã ca tập
     * @param maKV Mã khu vực
     * @return LichTap được tạo hoặc null nếu thất bại
     */
    LichTap createPTSchedule(String maNV, String maKH, String thu, String caTap, String maKV);
    
    /**
     * Kiểm tra xung đột lịch
     * @param maNV Mã trainer
     * @param thu Thứ trong tuần
     * @param caTap Mã ca tập
     * @return true nếu có xung đột
     */
    boolean hasScheduleConflict(String maNV, String thu, String caTap);
    
    /**
     * Generate mã lịch tập tiếp theo
     * @return Mã lịch tập mới
     */
    String generateNextMaLT();
} 