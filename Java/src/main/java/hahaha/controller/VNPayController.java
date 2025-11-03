package hahaha.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Types;

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
@RequestMapping("/api/vnpay")
public class VNPayController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private ChiTietDangKyDichVuRepository chiTietRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private LopRepository lopRepository;

    @Autowired
    private DataSource dataSource;

    // -------------------- TẠO LINK THANH TOÁN --------------------
    @PostMapping("/pay/{maHD}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPayment(@PathVariable String maHD, HttpServletRequest request)
            throws UnsupportedEncodingException {

        HoaDon hoaDon = hoaDonService.timMaHD(maHD);
        if (hoaDon == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy hóa đơn: " + maHD));

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        long amount = (long) (hoaDon.getTongTien() * 100);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "VNBANK");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + maHD);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_IpAddr", VNPayConfig.getIpAddress(request));
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // Tạo query string và hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString())).append('&');
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString())).append('&');
            }
        }
        if (hashData.length() > 0) hashData.setLength(hashData.length() - 1);
        if (query.length() > 0) query.setLength(query.length() - 1);

        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + query + "&vnp_SecureHash=" + vnp_SecureHash;

        return ResponseEntity.ok(Map.of(
                "paymentUrl", paymentUrl,
                "amount", hoaDon.getTongTien(),
                "orderId", maHD
        ));
    }

    // -------------------- CALLBACK TỪ VNPAY --------------------
    @GetMapping("/return")
    public ResponseEntity<?> ketQuaThanhToan(HttpServletRequest request) {
        Map<String, String> vnpParams = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> vnpParams.put(key, values[0]));

        String maHD = Optional.ofNullable(vnpParams.get("vnp_OrderInfo"))
                .map(s -> s.replace("Thanh toan don hang:", ""))
                .orElse(null);

        String vnpResponseCode = vnpParams.get("vnp_ResponseCode");
        String vnpTransactionStatus = vnpParams.get("vnp_TransactionStatus");
        String vnpSecureHash = vnpParams.get("vnp_SecureHash");

        boolean isValidSignature = validateVNPaySignature(vnpParams, vnpSecureHash);
        if (!isValidSignature)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Chữ ký không hợp lệ", "orderId", maHD));

        if ("00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus)) {
            hoaDonService.thanhToan(maHD);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thanh toán thành công",
                    "orderId", maHD
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", getVNPayErrorMessage(vnpResponseCode),
                    "orderId", maHD
            ));
        }
    }

    // -------------------- API CẬP NHẬT TRAINER/CLASS --------------------
    @PostMapping("/update-assignments")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateTrainerAndClassAssignments(
            @RequestParam String maHD,
            @RequestBody Map<String, Map<String, String>> serviceDetails) {
        try {
            HoaDon hoaDon = hoaDonService.timMaHD(maHD);
            if (hoaDon == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy hóa đơn"));

            int successCount = 0;
            for (ChiTietDangKyDichVu ct : hoaDon.getDsChiTiet()) {
                String maDV = ct.getDichVu().getMaDV();
                Map<String, String> detail = serviceDetails.get(maDV);
                if (detail == null) continue;

                if ("PT".equals(ct.getDichVu().getLoaiDV()) && detail.containsKey("trainerId")) {
                    if (updateTrainerForCTDK(ct.getMaCTDK(), detail.get("trainerId")))
                        successCount++;
                } else if ("Lop".equals(ct.getDichVu().getLoaiDV()) && detail.containsKey("classId")) {
                    if (updateClassForCTDK(ct.getMaCTDK(), detail.get("classId")))
                        successCount++;
                }
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật thành công " + successCount + "/" + hoaDon.getDsChiTiet().size() + " dịch vụ"
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Lỗi khi cập nhật trainer/class: " + e.getMessage()
            ));
        }
    }

    // -------------------- VALIDATE SIGNATURE --------------------
    private boolean validateVNPaySignature(Map<String, String> vnpParams, String vnpSecureHash) {
        try {
            Map<String, String> paramsToValidate = new HashMap<>(vnpParams);
            paramsToValidate.remove("vnp_SecureHash");

            List<String> fieldNames = new ArrayList<>(paramsToValidate.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            for (String fieldName : fieldNames) {
                String fieldValue = paramsToValidate.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()))
                            .append('&');
                }
            }
            if (hashData.length() > 0) hashData.setLength(hashData.length() - 1);

            String calculated = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
            return calculated.equalsIgnoreCase(vnpSecureHash);
        } catch (Exception e) {
            return false;
        }
    }

    private String getVNPayErrorMessage(String code) {
        switch (code) {
            case "00": return "Giao dịch thành công";
            case "24": return "Khách hàng hủy giao dịch";
            case "51": return "Tài khoản không đủ số dư";
            case "65": return "Vượt quá hạn mức giao dịch trong ngày";
            default: return "Lỗi khác: " + code;
        }
    }

    // -------------------- HELPER: CẬP NHẬT TRAINER/CLASS --------------------
    private boolean updateTrainerForCTDK(String maCTDK, String trainerId) {
        try (Connection conn = dataSource.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{call proc_cap_nhat_trainer_cho_ctdk(?, ?, ?, ?)}");
            stmt.setString(1, maCTDK);
            stmt.setString(2, trainerId);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.registerOutParameter(4, Types.VARCHAR);
            stmt.execute();
            return "SUCCESS".equals(stmt.getString(3));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean updateClassForCTDK(String maCTDK, String classId) {
        Optional<Lop> lop = lopRepository.findById(classId);
        Optional<ChiTietDangKyDichVu> ctOpt = chiTietRepository.findById(maCTDK);
        if (lop.isEmpty() || ctOpt.isEmpty()) return false;

        ChiTietDangKyDichVu ct = ctOpt.get();
        ct.setLop(lop.get());
        ct.setNhanVien(lop.get().getNhanVien());
        chiTietRepository.save(ct);
        return true;
    }
}
