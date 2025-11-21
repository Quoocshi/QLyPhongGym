package hahaha.controller;

import hahaha.DTO.ChiTietNhanVienDTO;
import hahaha.DTO.NhanVienDTO;
import hahaha.DTO.NhanVienRegisterDTO;
import hahaha.model.Account;
import hahaha.model.NhanVien;
import hahaha.model.RoleGroup;
import hahaha.repository.AccountRepository;
import hahaha.repository.RoleGroupRepository;
import hahaha.service.NhanVienService;
import hahaha.enums.LoaiNhanVien;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nhan-vien")
public class QlyNhanVienController {

    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleGroupRepository roleGroupRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔹 Lấy danh sách nhân viên
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> getAllNhanVien() {
        try {
            List<NhanVienDTO> list = nhanVienService.getAll().stream()
                    .map(nv -> {
                        NhanVienDTO dto = new NhanVienDTO();
                        dto.setMaNV(nv.getMaNV());
                        dto.setHoTen(nv.getTenNV());
                        return dto;
                    })
                    .toList();

            if (list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi lấy danh sách nhân viên: " + e.getMessage()));
        }
    }

    // 🔹 Xem chi tiết nhân viên
    @GetMapping("/{maNV}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> getChiTietNhanVien(@PathVariable String maNV) {
        try {
            NhanVien nv = nhanVienService.findById(maNV);
            if (nv == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy nhân viên với mã " + maNV));
            }

            ChiTietNhanVienDTO dto = new ChiTietNhanVienDTO();
            dto.setMaNV(nv.getMaNV());
            dto.setTenNV(nv.getTenNV());
            dto.setEmail(nv.getEmail());
            dto.setNgaySinh(nv.getNgaySinh());
            dto.setGioiTinh(nv.getGioiTinh());
            dto.setNgayVaoLam(nv.getNgayVaoLam());
            dto.setLoaiNV(nv.getLoaiNV() != null ? nv.getLoaiNV().name() : null);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi lấy chi tiết nhân viên: " + e.getMessage()));
        }
    }


    // 🔹 Thêm nhân viên
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addNhanVien(@RequestBody NhanVienRegisterDTO dto) {
        try {
            // Tạo NhanVien và Account từ DTO
            NhanVien nhanVien = nhanVienService.createFromDTO(dto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Thêm nhân viên thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    // 🔹 Cập nhật nhân viên
    @PutMapping("/{maNV}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateNhanVien(@PathVariable String maNV,
                                            @RequestBody NhanVien nhanVienUpdate) {
        try {
            NhanVien nhanVien = nhanVienService.findById(maNV);
            if (nhanVien == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy nhân viên"));
            }

            if (accountRepository.existsByEmail(nhanVienUpdate.getEmail())
                    && !nhanVien.getEmail().equals(nhanVienUpdate.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email đã tồn tại trong hệ thống"));
            }

            // Cập nhật thông tin nhân viên
            nhanVien.setTenNV(nhanVienUpdate.getTenNV());
            nhanVien.setNgaySinh(nhanVienUpdate.getNgaySinh());
            nhanVien.setGioiTinh(nhanVienUpdate.getGioiTinh());
            nhanVien.setEmail(nhanVienUpdate.getEmail());
            nhanVien.setNgayVaoLam(nhanVienUpdate.getNgayVaoLam());
            nhanVien.setLoaiNV(nhanVienUpdate.getLoaiNV());
            nhanVienService.updateNhanVien(nhanVien);

            // Cập nhật account
            Account account = accountRepository.findByNhanVien_MaNV(maNV);
            if (account != null) {
                account.setUserName(nhanVienUpdate.getEmail());
                Long roleGroupId = getRoleGroupIdByLoaiNV(nhanVienUpdate.getLoaiNV().name());
                RoleGroup roleGroup = roleGroupRepository.findById(roleGroupId).orElse(null);
                if (roleGroup != null) {
                    account.setRoleGroup(roleGroup);
                }
                account.setUpdatedAt(LocalDateTime.now());
                accountRepository.save(account);
            }

            return ResponseEntity.ok(Map.of("message", "Cập nhật nhân viên thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi cập nhật nhân viên", "message", e.getMessage()));
        }
    }

    // 🔹 Xóa nhân viên
    @DeleteMapping("/{maNV}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteNhanVien(@PathVariable String maNV) {
        try {
            Boolean result = nhanVienService.deleteNhanVien(maNV);
            if (result) {
                return ResponseEntity.ok(Map.of("message", "Xóa nhân viên thành công"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy nhân viên để xóa"));
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && (msg.contains("foreign key") || msg.contains("constraint"))) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Không thể xóa nhân viên này vì đã có dữ liệu liên quan"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi xóa nhân viên", "message", msg));
        }
    }

    // Helper method để lấy role group ID
    private Long getRoleGroupIdByLoaiNV(String loaiNV) {
        return switch (loaiNV) {
            case "QuanLy" -> 1L; // ADMIN
            case "LeTan" -> 2L;  // STAFF
            case "Trainer" -> 4L; // TRAINER
            case "PhongTap" -> 2L; // STAFF
            default -> 2L;
        };
    }
}
