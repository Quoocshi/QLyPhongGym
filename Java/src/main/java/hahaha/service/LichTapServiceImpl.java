package hahaha.service;

import java.time.LocalDate;
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
            System.out.println("=== TẠO LỊCH PT LẶP LẠI CHO TOÀN BỘ THỜI HẠN ===");
            System.out.println("Trainer: " + maNV);
            System.out.println("Khách hàng: " + maKH);
            System.out.println("Ngày tập đầu tiên: " + ngayTap);
            System.out.println("Ca tập: " + caTap);
            System.out.println("Khu vực: " + maKV);
            
            // Kiểm tra ngày tập có nằm trong thời hạn PT của khách hàng không
            System.out.println("🔍 Kiểm tra thời hạn PT...");
            if (!isDateWithinPTPeriod(maKH, ngayTap)) {
                System.out.println("❌ Ngày tập không nằm trong thời hạn PT của khách hàng");
                return null;
            }
            System.out.println("✅ Ngày tập hợp lệ");
            
            // Parse ngày từ yyyy-MM-dd
            LocalDate ngayBatDau = LocalDate.parse(ngayTap);
            
            // Tính thứ trong tuần (1=CN, 2=T2, ..., 7=T7)
            int dayOfWeek = ngayBatDau.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
            String thuValue;
            if (dayOfWeek == 7) {
                thuValue = "CN"; // Chủ nhật
            } else {
                thuValue = String.valueOf(dayOfWeek + 1); // T2=2, T3=3, ..., T7=7
            }
            System.out.println("🗓️ Thứ trong tuần: " + thuValue);
            
            // Lấy thông tin thời hạn PT của khách hàng
            KhachHang khachHang = khachHangRepository.findById(maKH).orElse(null);
            if (khachHang == null) {
                System.out.println("❌ Không tìm thấy khách hàng: " + maKH);
                return null;
            }
            
            LocalDate ngayKT = getKhachHangPTEndDate(maKH);
            if (ngayKT == null) {
                System.out.println("❌ Không tìm thấy thời hạn PT của khách hàng");
                return null;
            }
            System.out.println("📅 Thời hạn PT: " + ngayBatDau + " → " + ngayKT);
            
            // Kiểm tra xung đột lịch cho thứ này
            System.out.println("🔍 Kiểm tra xung đột lịch...");
            if (hasScheduleConflict(maNV, thuValue, caTap)) {
                System.out.println("❌ Xung đột lịch: Trainer " + maNV + " đã có lịch vào " + thuValue + " ca " + caTap);
                return null;
            }
            System.out.println("✅ Không có xung đột lịch");
            
            // Tạo lịch tập đầu tiên (sẽ trả về cho response)
            System.out.println("🔧 Tạo lịch tập đầu tiên...");
            LichTap firstLichTap = null;
            
            // Lặp qua từng tuần trong thời hạn PT
            LocalDate currentDate = ngayBatDau;
            int count = 0;
            while (!currentDate.isAfter(ngayKT)) {
                // Tạo lịch tập cho ngày này
                LichTap lichTap = new LichTap();
                
                String maLT = generateNextMaLTWithLogging();
                lichTap.setMaLT(maLT);
                lichTap.setLoaiLich("PT");
                lichTap.setThu(thuValue); // Lưu thứ trong tuần (VD: "2", "3", "CN")
                lichTap.setTrangThai("Dang mo");
                
                // Set trainer
                NhanVien trainer = nhanVienRepository.findById(maNV).orElse(null);
                if (trainer == null) {
                    System.out.println("❌ Không tìm thấy trainer: " + maNV);
                    return null;
                }
                lichTap.setNhanVien(trainer);
                
                // Set khách hàng
                lichTap.setKhachHang(khachHang);
                
                // Set ca tập
                CaTap ca = caTapRepository.findById(caTap).orElse(null);
                if (ca == null) {
                    System.out.println("❌ Không tìm thấy ca tập: " + caTap);
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
                
                // Lưu vào database
                LichTap savedLichTap = lichTapRepository.save(lichTap);
                count++;
                System.out.println("✅ Đã tạo lịch tập " + count + ": " + savedLichTap.getMaLT() + " cho ngày " + currentDate + " (" + thuValue + ")");
                
                // Lưu lịch đầu tiên để trả về
                if (firstLichTap == null) {
                    firstLichTap = savedLichTap;
                }
                
                // Chuyển sang tuần sau (cùng thứ)
                currentDate = currentDate.plusWeeks(1);
            }
            
            System.out.println("🎉 Đã tạo tổng cộng " + count + " lịch tập lặp lại mỗi tuần (" + thuValue + ") trong thời hạn PT");
            
            if (firstLichTap != null) {
                return firstLichTap;
            } else {
                System.out.println("❌ Không tạo được lịch tập nào");
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
    private LocalDate getKhachHangPTEndDate(String maKH) {
        try {
            // Lấy thông tin PT của khách hàng
            List<ChiTietDangKyDichVu> ptServices = chiTietDangKyDichVuRepository.findPTCustomersByCustomer(maKH);
            
            if (ptServices == null || ptServices.isEmpty()) {
                return null;
            }
            
            // Tìm ngày kết thúc gần nhất (active PT)
            LocalDate latestEndDate = null;
            for (ChiTietDangKyDichVu pt : ptServices) {
                if (pt.getNgayKT() != null) {
                    LocalDate endDate = pt.getNgayKT().toLocalDate();
                    if (latestEndDate == null || endDate.isAfter(latestEndDate)) {
                        latestEndDate = endDate;
                    }
                }
            }
            
            return latestEndDate;
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy ngày kết thúc PT: " + e.getMessage());
            return null;
        }
    }
    
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
    
    @Override
    public LichTap dungLichTap(String maLT, String ngayDung) {
        try {
            System.out.println("=== DỪNG LỊCH TẬP ===");
            System.out.println("Mã lịch tập: " + maLT);
            System.out.println("Ngày dừng: " + ngayDung);
            
            // Tìm lịch tập
            LichTap lichTap = lichTapRepository.findById(maLT).orElse(null);
            if (lichTap == null) {
                System.out.println("❌ Không tìm thấy lịch tập: " + maLT);
                return null;
            }
            
            // Kiểm tra lịch đã bị hủy chưa
            if ("Huy".equals(lichTap.getTrangThai())) {
                System.out.println("❌ Lịch tập đã bị hủy, không thể dừng");
                return null;
            }
            
            // Cập nhật trạng thái (ngayDung không còn trong database)
            lichTap.setTrangThai("Tam dung");
            // Note: ngayDung column removed from database
            
            LichTap updated = lichTapRepository.save(lichTap);
            System.out.println("✅ Đã dừng lịch tập thành công");
            
            return updated;
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi dừng lịch tập: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public LichTap huyLichTap(String maLT) {
        try {
            System.out.println("=== HỦY LỊCH TẬP ===");
            System.out.println("Mã lịch tập: " + maLT);
            
            // Tìm lịch tập
            LichTap lichTap = lichTapRepository.findById(maLT).orElse(null);
            if (lichTap == null) {
                System.out.println("❌ Không tìm thấy lịch tập: " + maLT);
                return null;
            }
            
            // Cập nhật trạng thái thành Huy
            lichTap.setTrangThai("Huy");
            
            LichTap updated = lichTapRepository.save(lichTap);
            System.out.println("✅ Đã hủy lịch tập thành công");
            
            return updated;
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi hủy lịch tập: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
} 