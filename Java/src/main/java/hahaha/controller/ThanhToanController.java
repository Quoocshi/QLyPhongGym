package hahaha.controller;

import hahaha.DTO.ChiTietDichVuDTO;
import hahaha.DTO.HoaDonDTO;
import hahaha.model.HoaDon;
import hahaha.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/thanh-toan")
@RequiredArgsConstructor
public class ThanhToanController {

    private final HoaDonService hoaDonService;

    @GetMapping("/{maHD}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> hienThiThanhToan(@PathVariable String maHD) {
        try {
            HoaDon hoaDon = hoaDonService.timMaHD(maHD);
            if (hoaDon == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy hóa đơn: " + maHD);
            }

            String hoTen = hoaDon.getKhachHang().getHoTen();

            // Map chi tiết dịch vụ
            List<ChiTietDichVuDTO> dsChiTiet = new ArrayList<>();
            if (hoaDon.getDsChiTiet() != null) {
                for (var ct : hoaDon.getDsChiTiet()) {
                    ChiTietDichVuDTO dto = new ChiTietDichVuDTO();
                    dto.setMaDV(ct.getDichVu() != null ? ct.getDichVu().getMaDV() : null);
                    dto.setTenDichVu(ct.getDichVu() != null ? ct.getDichVu().getTenDV() : "N/A");
                    dto.setLoaiDichVu(ct.getDichVu() != null && ct.getDichVu().getLoaiDV() != null
                            ? ct.getDichVu().getLoaiDV().toString()
                            : null);
                    dto.setThoiHan(ct.getDichVu() != null ? ct.getDichVu().getThoiHan() : null);
                    dto.setNgayBD(ct.getNgayBD());
                    dto.setNgayKT(ct.getNgayKT());
                    dto.setGiaTien(ct.getDichVu() != null ? ct.getDichVu().getDonGia() : 0.0);
                    dto.setMaNV(ct.getNhanVien() != null ? ct.getNhanVien().getMaNV() : null);
                    dto.setTenNV(ct.getNhanVien() != null ? ct.getNhanVien().getTenNV() : null);
                    dto.setTenLop(ct.getLop() != null ? ct.getLop().getTenLop() : null);
                    dsChiTiet.add(dto);
                }
            }

            // Tạo DTO
            HoaDonDTO hoaDonDTO = new HoaDonDTO(
                    hoaDon.getMaHD(),
                    hoaDon.getTongTien(),
                    hoaDon.getNgayLap(),
                    hoaDon.getTrangThai(),
                    hoaDon.getNgayTT(),
                    dsChiTiet);

            // Trả về Map gồm hoTen và hoaDonDTO
            return ResponseEntity.ok(Map.of(
                    "hoTen", hoTen,
                    "hoaDon", hoaDonDTO));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi lấy thông tin hóa đơn: " + e.getMessage());
        }
    }

    // @PostMapping("/{maHD}")
    // @PreAuthorize("hasRole('USER')")
    // public String thucHienThanhToan(@PathVariable String maHD) {
    // try {
    // hoaDonService.thanhToan(maHD);
    // return "redirect:/thanh-toan/" + maHD + "?success=true";
    // } catch (Exception e) {
    // return "redirect:/thanh-toan/" + maHD + "?error=true";
    // }
    // }
}
