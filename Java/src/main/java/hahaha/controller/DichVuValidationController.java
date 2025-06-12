package hahaha.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hahaha.service.DichVuValidationService;
import hahaha.service.DichVuValidationService.ServiceValidationResult;

/**
 * REST Controller để xử lý validation dịch vụ
 */
@RestController
@RequestMapping("/api/dichvu-validation")
public class DichVuValidationController {

    @Autowired
    private DichVuValidationService dichVuValidationService;

    /**
     * Kiểm tra xem có thể đăng ký dịch vụ hay không
     * 
     * @param maDV Mã dịch vụ muốn đăng ký
     * @param cartServices Danh sách các mã dịch vụ đã có trong giỏ (cách nhau bởi dấu phẩy)
     * @return ValidationResponse
     */
    @PostMapping("/kiem-tra")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ValidationResponse> kiemTraDichVu(
            @RequestParam("maDV") String maDV,
            @RequestParam(value = "cartServices", required = false, defaultValue = "") String cartServices) {
        
        try {
            System.out.println("=== KIỂM TRA VALIDATION DỊCH VỤ ===");
            System.out.println("Mã DV muốn đăng ký: " + maDV);
            System.out.println("Cart services raw: '" + cartServices + "'");
            
            // Parse danh sách dịch vụ trong giỏ
            List<String> danhSachMaDV = new java.util.ArrayList<>();
            if (!cartServices.trim().isEmpty()) {
                danhSachMaDV.addAll(Arrays.asList(cartServices.split(",")));
            }
            
            System.out.println("Danh sách DV trong giỏ: " + danhSachMaDV);
            
            // Thêm dịch vụ muốn đăng ký vào danh sách để kiểm tra
            danhSachMaDV.add(maDV);
            
            // Kiểm tra validation cho dịch vụ TuDo
            ServiceValidationResult resultTuDo = dichVuValidationService.kiemTraDichVuTuDo(maDV, danhSachMaDV);
            if (!resultTuDo.isValid()) {
                System.out.println("❌ Validation TuDo failed: " + resultTuDo.getMessage());
                return ResponseEntity.ok(new ValidationResponse(false, resultTuDo.getMessage()));
            }
            
            // Kiểm tra validation cho dịch vụ Lop/PT  
            ServiceValidationResult resultLopPT = dichVuValidationService.kiemTraDichVuLopPT(maDV, danhSachMaDV);
            if (!resultLopPT.isValid()) {
                System.out.println("❌ Validation Lop/PT failed: " + resultLopPT.getMessage());
                return ResponseEntity.ok(new ValidationResponse(false, resultLopPT.getMessage()));
            }
            
            System.out.println("✅ Validation passed");
            return ResponseEntity.ok(new ValidationResponse(true, "Có thể đăng ký dịch vụ"));
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi validation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new ValidationResponse(false, "Lỗi hệ thống khi kiểm tra: " + e.getMessage()));
        }
    }

    /**
     * Class để trả về response validation
     */
    public static class ValidationResponse {
        private final boolean canRegister;
        private final String message;
        
        public ValidationResponse(boolean canRegister, String message) {
            this.canRegister = canRegister;
            this.message = message;
        }
        
        public boolean isCanRegister() {
            return canRegister;
        }
        
        public String getMessage() {
            return message;
        }
    }
} 