package hahaha.controller;

import hahaha.DTO.HoaDonDTO;
import hahaha.model.HoaDon;
import hahaha.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

            // Tạo DTO
            HoaDonDTO hoaDonDTO = new HoaDonDTO(
                    hoaDon.getMaHD(),
                    hoaDon.getTongTien(),
                    hoaDon.getNgayLap(),
                    hoaDon.getTrangThai(),
                    hoaDon.getNgayTT()
            );

            // Trả về Map gồm hoTen và hoaDonDTO
            return ResponseEntity.ok(Map.of(
                    "hoTen", hoTen,
                    "hoaDon", hoaDonDTO
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi lấy thông tin hóa đơn: " + e.getMessage());
        }
    }


    // @PostMapping("/{maHD}")
    // @PreAuthorize("hasRole('USER')")
    // public String thucHienThanhToan(@PathVariable String maHD) {
    //     try {
    //         hoaDonService.thanhToan(maHD);
    //         return "redirect:/thanh-toan/" + maHD + "?success=true";
    //     } catch (Exception e) {
    //         return "redirect:/thanh-toan/" + maHD + "?error=true";
    //     }
    // }
}

