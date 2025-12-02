package hahaha.controller;

import java.util.*;

import hahaha.service.MomoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import hahaha.model.HoaDon;
import hahaha.service.HoaDonService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/momo")
public class MomoController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private MomoService momoService;

    // -------------------- TẠO LINK THANH TOÁN MOMO --------------------
    @PostMapping("/pay/{maHD}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPayment(@PathVariable String maHD) {
        HoaDon hoaDon = hoaDonService.timMaHD(maHD);

        if (hoaDon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy hóa đơn: " + maHD));
        }

        boolean thanhToanThanhCong = hoaDonService.thanhToan(maHD);

        double amountDouble = hoaDon.getTongTien();
        String amount = String.valueOf((long) amountDouble);

        //String paymentUrl = momoService.createPaymentRequest(String.valueOf(amount));

        // Kiểm tra nếu MoMo trả về lỗi
        //if (paymentUrl.startsWith("ERROR:")) {
        //    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        //            .body(Map.of("error", paymentUrl.substring(6)));
        //}

        return ResponseEntity.ok(Map.of(
                "maHD", maHD,
                "amount", amount,
                //"paymentUrl", paymentUrl,
                "thanhToan", thanhToanThanhCong ? "Thanh toán thành công"
                                                : "Thanh toán không thành công"
        ));
    }



    // -------------------- CALLBACK --------------------
    @GetMapping("/return")
    public ResponseEntity<?> momoReturn(HttpServletRequest request) {

        String orderId = request.getParameter("orderId");
        String resultCode = request.getParameter("resultCode");

        if ("0".equals(resultCode)) {
            // Thanh toán thành công
            hoaDonService.thanhToan(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thanh toán MoMo thành công",
                    "orderId", orderId
            ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "success", false,
                        "message", "Thanh toán thất bại resultCode=" + resultCode
                ));
    }

    // -------------------- KIỂM TRA TRẠNG THÁI --------------------
    @GetMapping("/order-status/{orderId}")
    public String checkStatus(@PathVariable String orderId) {
        return momoService.checkPaymentStatus(orderId);
    }
}
