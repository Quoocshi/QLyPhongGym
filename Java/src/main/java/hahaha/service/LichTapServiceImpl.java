package hahaha.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hahaha.model.ChiTietDangKyDichVu;
import hahaha.model.CaTap;
import hahaha.model.KhachHang;
import hahaha.model.KhuVuc;
import hahaha.model.LichTap;
import hahaha.model.NhanVien;
import hahaha.repository.ChiTietDangKyDichVuRepository;
import hahaha.repository.CaTapRepository;
import hahaha.repository.KhachHangRepository;
import hahaha.repository.KhuVucRepository;
import hahaha.repository.LichTapRepository;
import hahaha.repository.NhanVienRepository;

@Service
@Transactional
public class LichTapServiceImpl implements LichTapService {
    
    @Autowired
    private LichTapRepository lichTapRepository;
    
    @Autowired
    private ChiTietDangKyDichVuRepository chiTietDangKyDichVuRepository;
    
    @Autowired
    private NhanVienRepository nhanVienRepository;
    
    @Autowired
    private KhachHangRepository khachHangRepository;
    
    @Autowired
    private CaTapRepository caTapRepository;
    
    @Autowired
    private KhuVucRepository khuVucRepository;
    
    @Override
    public List<LichTap> getLichTapByKhachHang(String maKH) {
        return lichTapRepository.findByKhachHang_MaKH(maKH);
    }
    
    @Override
    public List<LichTap> getLichTapByKhachHangAndLoai(String maKH, String loaiLich) {
        return lichTapRepository.findByKhachHang_MaKHAndLoaiLich(maKH, loaiLich);
    }
    
    @Override
    public List<LichTap> getAllLichTapByKhachHang(String maKH) {
        List<LichTap> allSchedules = new ArrayList<>();
        
        // Lấy lịch PT của khách hàng
        List<LichTap> ptSchedules = lichTapRepository.findPTScheduleByKhachHang(maKH);
        if (ptSchedules != null) {
            allSchedules.addAll(ptSchedules);
        }
        
        // Lấy lịch lớp mà khách hàng đã đăng ký
        List<LichTap> classSchedules = lichTapRepository.findClassScheduleByKhachHang(maKH);
        if (classSchedules != null) {
            allSchedules.addAll(classSchedules);
        }
        
        return allSchedules;
    }
    
    @Override
    public List<ChiTietDangKyDichVu> getPTCustomersByTrainer(String maNV) {
        return chiTietDangKyDichVuRepository.findPTCustomersByTrainer(maNV);
    }
    
    @Override
    public List<LichTap> getPTScheduleByTrainer(String maNV) {
        return lichTapRepository.findPTScheduleByTrainer(maNV);
    }
    
    @Override
    public LichTap createPTSchedule(String maNV, String maKH, String thu, String caTap, String maKV) {
        try {
            // Kiểm tra xung đột lịch
            if (hasScheduleConflict(maNV, thu, caTap)) {
                System.out.println("Xung đột lịch: Trainer " + maNV + " đã có lịch vào " + thu + " ca " + caTap);
                return null;
            }
            
            // Tạo lịch tập mới
            LichTap lichTap = new LichTap();
            lichTap.setMaLT(generateNextMaLT());
            lichTap.setLoaiLich("PT");
            lichTap.setThu(thu);
            lichTap.setTrangThai("Dang mo");
            
            // Set trainer
            NhanVien trainer = nhanVienRepository.findById(maNV).orElse(null);
            if (trainer == null) {
                System.out.println("Không tìm thấy trainer: " + maNV);
                return null;
            }
            lichTap.setNhanVien(trainer);
            
            // Set khách hàng
            KhachHang khachHang = khachHangRepository.findById(maKH).orElse(null);
            if (khachHang == null) {
                System.out.println("Không tìm thấy khách hàng: " + maKH);
                return null;
            }
            lichTap.setKhachHang(khachHang);
            
            // Set ca tập
            CaTap ca = caTapRepository.findById(caTap).orElse(null);
            if (ca == null) {
                System.out.println("Không tìm thấy ca tập: " + caTap);
                return null;
            }
            lichTap.setCaTap(ca);
            
            // Set khu vực (optional)
            if (maKV != null && !maKV.isEmpty()) {
                KhuVuc khuVuc = khuVucRepository.findById(maKV).orElse(null);
                if (khuVuc != null) {
                    lichTap.setKhuVuc(khuVuc);
                }
            }
            
            return lichTapRepository.save(lichTap);
            
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo lịch PT: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean hasScheduleConflict(String maNV, String thu, String caTap) {
        Long count = lichTapRepository.countConflictingSchedules(maNV, caTap, thu);
        return count != null && count > 0;
    }
    
    /**
     * Tạo mã lịch tập tiếp theo
     */
    @Override
    public String generateNextMaLT() {
        Integer maxNumber = lichTapRepository.findMaxLichTapNumber();
        int nextNumber = (maxNumber != null) ? maxNumber + 1 : 1;
        return String.format("LT%03d", nextNumber);
    }
    
    /**
     * Tạo mã lịch tập tiếp theo với logging
     */
    private String generateNextMaLTWithLogging() {
        try {
            System.out.println("🔧 Generating next MaLT...");
            String lastMaLT = lichTapRepository.findLastMaLT();
            System.out.println("Last MaLT from database: " + lastMaLT);
            
            if (lastMaLT == null || lastMaLT.isEmpty()) {
                System.out.println("No existing MaLT found, starting with LT001");
                return "LT001";
            }
            
            // Lấy số từ mã cuối cùng (VD: LT025 -> 25)
            String numberPart = lastMaLT.substring(2);
            System.out.println("Number part extracted: " + numberPart);
            
            int nextNumber = Integer.parseInt(numberPart) + 1;
            System.out.println("Next number: " + nextNumber);
            
            String nextMaLT = String.format("LT%03d", nextNumber);
            System.out.println("Generated next MaLT: " + nextMaLT);
            
            return nextMaLT;
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo mã lịch tập: " + e.getMessage());
            e.printStackTrace();
            // Fallback: tạo mã ngẫu nhiên
            String fallbackMaLT = "LT" + System.currentTimeMillis() % 1000;
            System.out.println("Using fallback MaLT: " + fallbackMaLT);
            return fallbackMaLT;
        }
    }
    
    @Override
    public LichTap createPTScheduleWithDate(String maNV, String maKH, String ngayTap, String caTap, String maKV) {
        try {
            System.out.println("=== TẠO LỊCH PT VỚI NGÀY CỤ THỂ ===");
            System.out.println("Trainer: " + maNV);
            System.out.println("Khách hàng: " + maKH);
            System.out.println("Ngày tập: " + ngayTap);
            System.out.println("Ca tập: " + caTap);
            System.out.println("Khu vực: " + maKV);
            
            // Chuyển đổi ngày từ yyyy-MM-dd sang dd/MM/yyyy để lưu vào cột Thu
            String ngayTapFormatted = convertDateFormat(ngayTap);
            System.out.println("Ngày tập formatted: " + ngayTapFormatted);
            
            // Kiểm tra xung đột lịch cho ngày cụ thể
            System.out.println("🔍 Kiểm tra xung đột lịch...");
            if (hasScheduleConflictForDate(maNV, ngayTapFormatted, caTap)) {
                System.out.println("❌ Xung đột lịch: Trainer " + maNV + " đã có lịch vào " + ngayTapFormatted + " ca " + caTap);
                return null;
            }
            System.out.println("✅ Không có xung đột lịch");
            
            // Kiểm tra ngày tập có nằm trong thời hạn PT của khách hàng không
            System.out.println("🔍 Kiểm tra thời hạn PT...");
            if (!isDateWithinPTPeriod(maKH, ngayTap)) {
                System.out.println("❌ Ngày tập không nằm trong thời hạn PT của khách hàng");
                return null;
            }
            System.out.println("✅ Ngày tập hợp lệ");
            
            // Tạo lịch tập mới
            System.out.println("🔧 Tạo đối tượng LichTap...");
            LichTap lichTap = new LichTap();
            
            String maLT = generateNextMaLTWithLogging();
            lichTap.setMaLT(maLT);
            lichTap.setLoaiLich("PT");
            lichTap.setThu(ngayTapFormatted); // Lưu ngày dưới dạng dd/MM/yyyy
            lichTap.setTrangThai("Dang mo");
            
            // Set trainer
            System.out.println("🔍 Tìm trainer: " + maNV);
            NhanVien trainer = nhanVienRepository.findById(maNV).orElse(null);
            if (trainer == null) {
                System.out.println("❌ Không tìm thấy trainer: " + maNV);
                return null;
            }
            System.out.println("✅ Tìm thấy trainer: " + trainer.getTenNV());
            lichTap.setNhanVien(trainer);
            
            // Set khách hàng
            System.out.println("🔍 Tìm khách hàng: " + maKH);
            KhachHang khachHang = khachHangRepository.findById(maKH).orElse(null);
            if (khachHang == null) {
                System.out.println("❌ Không tìm thấy khách hàng: " + maKH);
                return null;
            }
            System.out.println("✅ Tìm thấy khách hàng: " + khachHang.getHoTen());
            lichTap.setKhachHang(khachHang);
            
            // Set ca tập
            System.out.println("🔍 Tìm ca tập: " + caTap);
            CaTap ca = caTapRepository.findById(caTap).orElse(null);
            if (ca == null) {
                System.out.println("❌ Không tìm thấy ca tập: " + caTap);
                return null;
            }
            System.out.println("✅ Tìm thấy ca tập: " + ca.getTenCa());
            lichTap.setCaTap(ca);
            
            // Set khu vực (optional)
            if (maKV != null && !maKV.isEmpty()) {
                System.out.println("🔍 Tìm khu vực: " + maKV);
                KhuVuc khuVuc = khuVucRepository.findById(maKV).orElse(null);
                if (khuVuc != null) {
                    System.out.println("✅ Tìm thấy khu vực: " + khuVuc.getTenKhuVuc());
                    lichTap.setKhuVuc(khuVuc);
                } else {
                    System.out.println("⚠️ Không tìm thấy khu vực: " + maKV);
                }
            } else {
                System.out.println("ℹ️ Không chọn khu vực");
            }
            
            // Debug thông tin trước khi save
            System.out.println("📋 Thông tin LichTap trước khi save:");
            System.out.println("  - MaLT: " + lichTap.getMaLT());
            System.out.println("  - LoaiLich: " + lichTap.getLoaiLich());
            System.out.println("  - Thu: " + lichTap.getThu());
            System.out.println("  - TrangThai: " + lichTap.getTrangThai());
            System.out.println("  - Trainer: " + (lichTap.getNhanVien() != null ? lichTap.getNhanVien().getMaNV() : "null"));
            System.out.println("  - KhachHang: " + (lichTap.getKhachHang() != null ? lichTap.getKhachHang().getMaKH() : "null"));
            System.out.println("  - CaTap: " + (lichTap.getCaTap() != null ? lichTap.getCaTap().getMaCa() : "null"));
            System.out.println("  - KhuVuc: " + (lichTap.getKhuVuc() != null ? lichTap.getKhuVuc().getMaKV() : "null"));
            
            // Lưu vào database
            System.out.println("💾 Đang lưu vào database...");
            LichTap savedLichTap = lichTapRepository.save(lichTap);
            
            if (savedLichTap != null) {
                System.out.println("✅ Đã lưu thành công lịch tập: " + savedLichTap.getMaLT() + " với ngày: " + savedLichTap.getThu());
                
                // Verify bằng cách query lại
                System.out.println("🔍 Verify bằng cách query lại...");
                LichTap verifyLichTap = lichTapRepository.findById(savedLichTap.getMaLT()).orElse(null);
                if (verifyLichTap != null) {
                    System.out.println("✅ Verify thành công: Tìm thấy lịch tập " + verifyLichTap.getMaLT() + " trong database");
                } else {
                    System.out.println("❌ Verify thất bại: Không tìm thấy lịch tập trong database");
                }
                
                return savedLichTap;
            } else {
                System.out.println("❌ Lưu thất bại: savedLichTap is null");
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo lịch PT với ngày cụ thể: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Chuyển đổi định dạng ngày từ yyyy-MM-dd sang dd/MM/yyyy
     */
    private String convertDateFormat(String dateString) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateString);
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return date.format(formatter);
        } catch (Exception e) {
            System.err.println("Lỗi chuyển đổi định dạng ngày: " + e.getMessage());
            return dateString;
        }
    }
    
    /**
     * Kiểm tra xung đột lịch cho ngày cụ thể
     */
    private boolean hasScheduleConflictForDate(String maNV, String ngayTap, String caTap) {
        Long count = lichTapRepository.countConflictingSchedulesForDate(maNV, caTap, ngayTap);
        return count != null && count > 0;
    }
    
    /**
     * Kiểm tra ngày tập có nằm trong thời hạn PT của khách hàng không
     */
    private boolean isDateWithinPTPeriod(String maKH, String ngayTap) {
        try {
            System.out.println("🔍 Kiểm tra thời hạn PT cho khách hàng: " + maKH + ", ngày: " + ngayTap);
            java.time.LocalDate selectedDate = java.time.LocalDate.parse(ngayTap);
            System.out.println("Selected date parsed: " + selectedDate);
            
            // Lấy thông tin PT của khách hàng
            List<ChiTietDangKyDichVu> ptServices = chiTietDangKyDichVuRepository.findPTCustomersByCustomer(maKH);
            System.out.println("Số dịch vụ PT của khách hàng: " + (ptServices != null ? ptServices.size() : "null"));
            
            if (ptServices == null || ptServices.isEmpty()) {
                System.out.println("❌ Khách hàng chưa đăng ký dịch vụ PT nào");
                return false;
            }
            
            for (ChiTietDangKyDichVu pt : ptServices) {
                System.out.println("Kiểm tra dịch vụ PT: " + pt.getMaCTDK());
                System.out.println("  - Ngày bắt đầu: " + pt.getNgayBD());
                System.out.println("  - Ngày kết thúc: " + pt.getNgayKT());
                System.out.println("  - Dịch vụ: " + (pt.getDichVu() != null ? pt.getDichVu().getTenDV() : "null"));
                System.out.println("  - Trạng thái HĐ: " + (pt.getHoaDon() != null ? pt.getHoaDon().getTrangThai() : "null"));
                
                java.time.LocalDate startDate = pt.getNgayBD().toLocalDate();
                java.time.LocalDate endDate = pt.getNgayKT().toLocalDate();
                
                System.out.println("  - Start date: " + startDate);
                System.out.println("  - End date: " + endDate);
                System.out.println("  - Selected date: " + selectedDate);
                System.out.println("  - Is after start: " + !selectedDate.isBefore(startDate));
                System.out.println("  - Is before end: " + !selectedDate.isAfter(endDate));
                
                if (!selectedDate.isBefore(startDate) && !selectedDate.isAfter(endDate)) {
                    System.out.println("✅ Ngày " + selectedDate + " nằm trong khoảng thời gian PT: " + startDate + " - " + endDate);
                    return true; // Ngày nằm trong khoảng thời gian PT
                } else {
                    System.out.println("⚠️ Ngày " + selectedDate + " KHÔNG nằm trong khoảng thời gian PT: " + startDate + " - " + endDate);
                }
            }
            
            System.out.println("❌ Ngày " + selectedDate + " không nằm trong bất kỳ khoảng thời gian PT nào");
            return false;
        } catch (Exception e) {
            System.err.println("❌ Lỗi kiểm tra thời hạn PT: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 