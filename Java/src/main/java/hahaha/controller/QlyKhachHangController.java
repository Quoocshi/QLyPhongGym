package hahaha.controller;

import hahaha.DTO.ChiTietKhachHangDTO;
import hahaha.DTO.KhachHangDTO;
import hahaha.model.KhachHang;
import hahaha.service.KhachHangService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/khach-hang")
public class QlyKhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    // 🔹 Lấy danh sách khách hàng
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> getAllCustomers() {
        try {
            List<KhachHangDTO> customers = khachHangService.getAll().stream()
                    .map(kh -> {
                        KhachHangDTO dto = new KhachHangDTO();
                        dto.setMaKH(kh.getMaKH());
                        dto.setHoTen(kh.getHoTen());
                        return dto;
                    })
                    .toList();

            if (customers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi lấy danh sách khách hàng: " + e.getMessage()));
        }
    }

    // Lấy thông tin chi tiết khách hàng
    @GetMapping("/{maKH}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> getCustomerDetails(@PathVariable String maKH) {
        try {
            KhachHang kh = khachHangService.findById(maKH);
            if (kh == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy khách hàng với mã " + maKH));
            }

            ChiTietKhachHangDTO dto = new ChiTietKhachHangDTO();
            dto.setMaKH(kh.getMaKH());
            dto.setHoTen(kh.getHoTen());
            dto.setSoDienThoai(kh.getSoDienThoai());
            dto.setEmail(kh.getEmail());
            dto.setDiaChi(kh.getDiaChi());
            dto.setReferalCode(kh.getReferralCode());
            dto.setNgaySinh(kh.getNgaySinh());
            dto.setGioiTinh(kh.getGioiTinh());

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi lấy thông tin khách hàng: " + e.getMessage()));
        }
    }



    // 🔹 Tìm kiếm khách hàng theo keyword
    @GetMapping("/tim-kiem/{keyword}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> searchCustomers(@PathVariable String keyword) {
        try {
            keyword = keyword.trim().replaceAll("\\s+", " ");
            List<ChiTietKhachHangDTO> customers = khachHangService.searchCustomers(keyword).stream()
                    .map(kh -> {
                        ChiTietKhachHangDTO dto = new ChiTietKhachHangDTO();
                        dto.setMaKH(kh.getMaKH());
                        dto.setHoTen(kh.getHoTen());
                        dto.setSoDienThoai(kh.getSoDienThoai());
                        dto.setEmail(kh.getEmail());
                        dto.setDiaChi(kh.getDiaChi());
                        dto.setReferalCode(kh.getReferralCode());
                        dto.setNgaySinh(kh.getNgaySinh());
                        dto.setGioiTinh(kh.getGioiTinh());
                        return dto;
                    })
                    .toList();

            if (customers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy khách hàng nào phù hợp"));
            }
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi tìm kiếm: " + e.getMessage()));
        }
    }

    // 🔹 Cập nhật thông tin khách hàng
    @PutMapping("/{maKH}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> updateCustomer(@PathVariable String maKH, @RequestBody KhachHang customer) {
        try {
            customer.setMaKH(maKH);
            boolean result = khachHangService.updateCustomer(customer);

            if (result) {
                return ResponseEntity.ok(Map.of("message", "Cập nhật thông tin khách hàng thành công"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Không thể cập nhật khách hàng"));
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy khách hàng với mã " + maKH));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    // 🔹 Xóa khách hàng
    @DeleteMapping("/{maKH}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> deleteCustomer(@PathVariable String maKH) {
        try {
            boolean deleted = khachHangService.deleteCustomer(maKH);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Xóa khách hàng thành công"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy khách hàng để xóa"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi xóa khách hàng: " + e.getMessage()));
        }
    }
}
