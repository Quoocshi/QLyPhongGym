package hahaha.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    @Override
    public String generateNextMaLT() {
        Integer maxNumber = lichTapRepository.findMaxLichTapNumber();
        int nextNumber = (maxNumber != null) ? maxNumber + 1 : 1;
        return String.format("LT%03d", nextNumber);
    }
} 