package hahaha.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Types;

import hahaha.model.PaymentRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import hahaha.config.VNPayConfig;
import hahaha.model.HoaDon;
import hahaha.model.ChiTietDangKyDichVu;
import hahaha.model.Lop;
import hahaha.service.HoaDonService;
import hahaha.repository.ChiTietDangKyDichVuRepository;
import hahaha.repository.NhanVienRepository;
import hahaha.repository.LopRepository;

import jakarta.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
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

        long amount = (long) hoaDon.getTongTien(); // MoMo không nhân 100

        String momoResponse = momoService.createPaymentRequest(String.valueOf(amount));

        return ResponseEntity.ok(Map.of(
                "maHD", maHD,
                "amount", amount,
                "momoResponse", momoResponse
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
