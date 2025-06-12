package hahaha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hahaha.enums.LoaiDichVu;
import hahaha.model.DichVu;
import hahaha.repository.DichVuRepository;

/**
 * Service để validate business rules cho việc đăng ký dịch vụ
 */
@Service
public class DichVuValidationService {

    @Autowired
    private DichVuRepository dichVuRepository;

    /**
     * Kiểm tra xem có thể đăng ký dịch vụ TuDo hay không
     * Quy tắc: Nếu đã có Lop hoặc PT của cùng bộ môn trong danh sách 
     * thì không được phép đăng ký TuDo của bộ môn đó
     * 
     * @param maDVTuDo Mã dịch vụ TuDo muốn đăng ký
     * @param danhSachMaDV Danh sách các mã dịch vụ trong giỏ hàng
     * @return ServiceValidationResult chứa kết quả validation
     */
    public ServiceValidationResult kiemTraDichVuTuDo(String maDVTuDo, List<String> danhSachMaDV) {
        try {
            // Lấy thông tin dịch vụ TuDo
            DichVu dichVuTuDo = dichVuRepository.findById(maDVTuDo).orElse(null);
            if (dichVuTuDo == null) {
                return new ServiceValidationResult(false, "Dịch vụ không tồn tại: " + maDVTuDo);
            }

            // Kiểm tra xem có phải dịch vụ TuDo không
            if (dichVuTuDo.getLoaiDV() != LoaiDichVu.TuDo) {
                return new ServiceValidationResult(true, "Dịch vụ không phải loại TuDo, có thể đăng ký bình thường");
            }

            String maBMTuDo = dichVuTuDo.getBoMon().getMaBM();
            
            // Kiểm tra từng dịch vụ trong giỏ hàng
            for (String maDV : danhSachMaDV) {
                if (maDV.equals(maDVTuDo)) {
                    continue; // Bỏ qua chính nó
                }
                
                DichVu dichVuTrongGio = dichVuRepository.findById(maDV).orElse(null);
                if (dichVuTrongGio == null) {
                    continue; // Bỏ qua dịch vụ không tồn tại
                }
                
                // Kiểm tra xem có phải cùng bộ môn không
                if (dichVuTrongGio.getBoMon().getMaBM().equals(maBMTuDo)) {
                    // Nếu trong giỏ đã có Lop hoặc PT của cùng bộ môn
                    if (dichVuTrongGio.getLoaiDV() == LoaiDichVu.Lop || 
                        dichVuTrongGio.getLoaiDV() == LoaiDichVu.PT) {
                        
                        String tenBoMon = dichVuTuDo.getBoMon().getTenBM();
                        String loaiDVConflict = dichVuTrongGio.getLoaiDV() == LoaiDichVu.Lop ? "Lớp" : "Personal Trainer";
                        
                        return new ServiceValidationResult(false, 
                            String.format("❌ Không thể đăng ký dịch vụ Tự Do %s vì bạn đã có dịch vụ %s của %s trong giỏ hàng. " +
                                        "Mỗi bộ môn chỉ được chọn một loại dịch vụ (Tự Do HOẶC Lớp/PT).", 
                                        tenBoMon, loaiDVConflict, tenBoMon));
                    }
                }
            }
            
            return new ServiceValidationResult(true, "Có thể đăng ký dịch vụ TuDo");
            
        } catch (Exception e) {
            return new ServiceValidationResult(false, "Lỗi khi kiểm tra validation: " + e.getMessage());
        }
    }
    
    /**
     * Kiểm tra xem có thể đăng ký dịch vụ Lop hoặc PT hay không
     * Quy tắc: 
     * 1. Nếu đã có TuDo của cùng bộ môn → không được đăng ký Lop/PT
     * 2. Nếu đã có PT của cùng bộ môn → không được đăng ký PT khác của bộ môn đó
     */
    public ServiceValidationResult kiemTraDichVuLopPT(String maDVLopPT, List<String> danhSachMaDV) {
        try {
            // Lấy thông tin dịch vụ Lop/PT
            DichVu dichVuLopPT = dichVuRepository.findById(maDVLopPT).orElse(null);
            if (dichVuLopPT == null) {
                return new ServiceValidationResult(false, "Dịch vụ không tồn tại: " + maDVLopPT);
            }

            // Kiểm tra xem có phải dịch vụ Lop hoặc PT không
            if (dichVuLopPT.getLoaiDV() == LoaiDichVu.TuDo) {
                return new ServiceValidationResult(true, "Dịch vụ là TuDo, có thể đăng ký bình thường");
            }

            String maBMLopPT = dichVuLopPT.getBoMon().getMaBM();
            String tenBoMon = dichVuLopPT.getBoMon().getTenBM();
            String loaiDVMuonDangKy = dichVuLopPT.getLoaiDV() == LoaiDichVu.Lop ? "Lớp" : "Personal Trainer";
            
            // Kiểm tra từng dịch vụ trong giỏ hàng
            for (String maDV : danhSachMaDV) {
                if (maDV.equals(maDVLopPT)) {
                    continue; // Bỏ qua chính nó
                }
                
                DichVu dichVuTrongGio = dichVuRepository.findById(maDV).orElse(null);
                if (dichVuTrongGio == null) {
                    continue; // Bỏ qua dịch vụ không tồn tại
                }
                
                // Kiểm tra xem có phải cùng bộ môn không
                if (dichVuTrongGio.getBoMon().getMaBM().equals(maBMLopPT)) {
                    // Quy tắc 1: Nếu trong giỏ đã có TuDo của cùng bộ môn
                    if (dichVuTrongGio.getLoaiDV() == LoaiDichVu.TuDo) {
                        return new ServiceValidationResult(false, 
                            String.format("❌ Không thể đăng ký dịch vụ %s %s vì bạn đã có dịch vụ Tự Do của %s trong giỏ hàng. " +
                                        "Mỗi bộ môn chỉ được chọn một loại dịch vụ (Tự Do HOẶC Lớp/PT).", 
                                        loaiDVMuonDangKy, tenBoMon, tenBoMon));
                    }
                    
                    // Quy tắc 2: Nếu muốn đăng ký PT và đã có PT khác của cùng bộ môn
                    if (dichVuLopPT.getLoaiDV() == LoaiDichVu.PT && dichVuTrongGio.getLoaiDV() == LoaiDichVu.PT) {
                        return new ServiceValidationResult(false, 
                            String.format("❌ Không thể đăng ký thêm dịch vụ Personal Trainer %s vì bạn đã có dịch vụ PT khác của %s trong giỏ hàng. " +
                                        "Mỗi bộ môn chỉ được chọn một dịch vụ Personal Trainer.", 
                                        tenBoMon, tenBoMon));
                    }
                }
            }
            
            return new ServiceValidationResult(true, "Có thể đăng ký dịch vụ Lop/PT");
            
        } catch (Exception e) {
            return new ServiceValidationResult(false, "Lỗi khi kiểm tra validation: " + e.getMessage());
        }
    }

    /**
     * Class để lưu kết quả validation
     */
    public static class ServiceValidationResult {
        private final boolean isValid;
        private final String message;
        
        public ServiceValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getMessage() {
            return message;
        }
    }
} 