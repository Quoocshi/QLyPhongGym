package hahaha.controller;

import java.util.List;
import java.util.Map;

import hahaha.DTO.DichVuDTO;
import hahaha.service.BoMonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import hahaha.enums.LoaiDichVu;
import hahaha.model.BoMon;
import hahaha.model.DichVu;
import hahaha.service.DichVuService;

@RestController
@RequestMapping("/api/quan-ly-dich-vu")
public class QlyDichVuController {

    @Autowired
    private DichVuService dichVuService;

    @Autowired
    private BoMonService boMonService;

    // Lấy danh sách dịch vụ
    @GetMapping("/danh-sach-dich-vu")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> getAllDichVu() {
        List<DichVuDTO> list = dichVuService.getAll().stream()
                .map(dv -> {
                    DichVuDTO dto = new DichVuDTO();
                    dto.setMaDV(dv.getMaDV());
                    dto.setTenDV(dv.getTenDV());
                    dto.setLoaiDV(dv.getLoaiDV().toString());
                    dto.setThoiHan(dv.getThoiHan());
                    dto.setDonGia(dv.getDonGia());
                    dto.setMaBM(dv.getBoMon() != null ? dv.getBoMon().getMaBM() : null);
                    return dto;
                }).toList();
        return ResponseEntity.ok(list);
    }

    // Thêm dịch vụ
    @PostMapping("/them-dich-vu")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> themDichVu(@RequestBody DichVu dv) {
        try {
            DichVu dichVu = new DichVu();
            dichVu.setMaDV(dv.getMaDV());
            dichVu.setTenDV(dv.getTenDV());
            dichVu.setLoaiDV(LoaiDichVu.valueOf(dv.getLoaiDV().toString()));
            dichVu.setThoiHan(dv.getThoiHan());
            dichVu.setDonGia(dv.getDonGia());

            BoMon boMon = boMonService.findByid(dv.getBoMon().getMaBM());
            dichVu.setBoMon(boMon);

            Boolean result = dichVuService.createDichVu(dichVu);
            if (result) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("message", "Thêm dịch vụ thành công!"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Không thể thêm dịch vụ"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Lấy chi tiết dịch vụ
    @GetMapping("/{maDV}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> getDichVu(@PathVariable String maDV) {
        DichVu dichVu = dichVuService.findById(maDV);
        DichVuDTO dto = new DichVuDTO();
        dto.setMaDV(dichVu.getMaDV());
        dto.setTenDV(dichVu.getTenDV());
        dto.setLoaiDV(dichVu.getLoaiDV().toString());
        dto.setThoiHan(dichVu.getThoiHan());
        dto.setDonGia(dichVu.getDonGia());
        dto.setMaBM(dichVu.getBoMon().getMaBM());
        if (dichVu == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy dịch vụ!"));
        }
        return ResponseEntity.ok(dto);
    }

    // Cập nhật dịch vụ
    @PutMapping("/{maDV}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> capNhatDichVu(@PathVariable String maDV, @RequestBody DichVu dv) {
        try {
            DichVu dichVu = dichVuService.findById(maDV);
            if (dichVu == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy dịch vụ!"));
            }

            dichVu.setTenDV(dv.getTenDV());
            dichVu.setLoaiDV(LoaiDichVu.valueOf(dv.getLoaiDV().toString()));
            dichVu.setThoiHan(dv.getThoiHan());
            dichVu.setDonGia(dv.getDonGia());
            dichVu.setVersion(dv.getVersion());

            BoMon boMon = boMonService.findByid(dv.getBoMon().getMaBM());
            dichVu.setBoMon(boMon);

            Boolean result = dichVuService.updateDichVu(dichVu);
            if (result) {
                return ResponseEntity.ok(Map.of("message", "Cập nhật dịch vụ thành công!"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Không thể cập nhật dịch vụ"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Xóa dịch vụ
    @DeleteMapping("/{maDV}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> xoaDichVu(@PathVariable String maDV) {
        try {
            Boolean result = dichVuService.deleteDichVu(maDV);
            if (result) {
                return ResponseEntity.ok(Map.of("message", "Xóa dịch vụ thành công!"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Không thể xóa dịch vụ này!"));
            }
        } catch (Exception e) {
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("constraint")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Không thể xóa dịch vụ vì đã có khách hàng đăng ký!"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", e.getMessage()));
            }
        }
    }
}
