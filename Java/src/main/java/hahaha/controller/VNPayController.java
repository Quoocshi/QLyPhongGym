package hahaha.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hahaha.config.VNPayConfig;
import hahaha.model.HoaDon;
import hahaha.service.HoaDonService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/vnpay")
public class VNPayController {
    @Autowired
    private HoaDonService hoaDonService;

    @PostMapping("/pay/{maHD}")
    @PreAuthorize("hasRole('USER')")
    public String createPayment(@PathVariable String maHD,HttpServletRequest request, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException{

        HoaDon hoaDon = hoaDonService.timMaHD(maHD);
        if (hoaDon == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy hóa đơn");
            return "redirect:/thanh-toan/" + maHD;

        }

        // String orderType = "other";
        // long amount = Integer.parseInt(req.getParameter("amount"))*100;
        // String bankCode = req.getParameter("bankCode");
        

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        // String vnp_IpAddr = VNPayConfig.getIpAddress(req);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        long amount = (long)(hoaDon.getTongTien() * 100);
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + maHD);
        vnp_Params.put("vnp_OrderType", "other");
        String ipAddress = VNPayConfig.getIpAddress(request);
        vnp_Params.put("vnp_IpAddr", ipAddress);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        
        // List fieldNames = new ArrayList(vnp_Params.keySet());
        // Collections.sort(fieldNames);
        // StringBuilder hashData = new StringBuilder();
        // StringBuilder query = new StringBuilder();
        // Iterator itr = fieldNames.iterator();
        // while (itr.hasNext()) {
        //     String fieldName = (String) itr.next();
        //     String fieldValue = (String) vnp_Params.get(fieldName);
        //     if ((fieldValue != null) && (fieldValue.length() > 0)) {
        //         //Build hash data
        //         hashData.append(fieldName);
        //         hashData.append('=');
        //         hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
        //         //Build query
        //         query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
        //         query.append('=');
        //         query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
        //         if (itr.hasNext()) {
        //             query.append('&');
        //             hashData.append('&');
        //         }
        //     }
        // }
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append('&');
                hashData.append('&');
            }
        }

        if (query.length() > 0)
            query.setLength(query.length() - 1);
        if (hashData.length() > 0)
            hashData.setLength(hashData.length() - 1);

        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);
        return "redirect:" + VNPayConfig.vnp_PayUrl + "?" + query.toString();
    }
    

    @GetMapping("/return")
    public String ketQuaThanhToan(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        System.out.println("🎯 VNPay ĐÃ GỌI CALLBACK /vnpay/return");
        
        // Lấy tất cả parameters từ VNPay
        Map<String, String> vnpParams = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> vnpParams.put(key, values[0]));
        
        // Log các parameters để debug
        System.out.println("📋 VNPay Parameters:");
        vnpParams.forEach((key, value) -> System.out.println(key + ": " + value));
        
        // Lấy mã hóa đơn từ vnp_OrderInfo
        String maHD = vnpParams.get("vnp_OrderInfo");
        if (maHD != null && maHD.startsWith("Thanh toan don hang:")) {
            maHD = maHD.replace("Thanh toan don hang:", "");
        }
        
        // Kiểm tra các thông tin quan trọng từ VNPay
        String vnpResponseCode = vnpParams.get("vnp_ResponseCode");
        String vnpTransactionStatus = vnpParams.get("vnp_TransactionStatus");
        String vnpSecureHash = vnpParams.get("vnp_SecureHash");
        
        System.out.println("🔍 Kiểm tra kết quả thanh toán:");
        System.out.println("Response Code: " + vnpResponseCode);
        System.out.println("Transaction Status: " + vnpTransactionStatus);
        System.out.println("Mã hóa đơn: " + maHD);
        
        // Xác thực chữ ký (signature) từ VNPay
        boolean isValidSignature = validateVNPaySignature(vnpParams, vnpSecureHash);
        
        if (!isValidSignature) {
            System.err.println("❌ Chữ ký không hợp lệ từ VNPay!");
            redirectAttributes.addFlashAttribute("error", "Giao dịch không hợp lệ!");
            return "redirect:/thanh-toan/" + maHD + "?error=invalid_signature";
        }
        
        // Kiểm tra kết quả thanh toán
        // vnp_ResponseCode = "00" AND vnp_TransactionStatus = "00" => Thành công
        if ("00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus)) {
            try {
                // Chỉ cập nhật trạng thái khi thanh toán thành công
                hoaDonService.thanhToan(maHD);
                System.out.println("✅ Cập nhật trạng thái thanh toán thành công cho hóa đơn: " + maHD);
                redirectAttributes.addFlashAttribute("success", "Thanh toán thành công!");
                return "redirect:/thanh-toan/" + maHD + "?success=true";
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi cập nhật trạng thái thanh toán: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xử lý thanh toán!");
                return "redirect:/thanh-toan/" + maHD + "?error=update_failed";
            }
        } else {
            // Thanh toán thất bại
            String errorMessage = getVNPayErrorMessage(vnpResponseCode);
            System.out.println("❌ Thanh toán thất bại: " + errorMessage);
            redirectAttributes.addFlashAttribute("error", "Thanh toán thất bại: " + errorMessage);
            return "redirect:/thanh-toan/" + maHD + "?error=payment_failed";
        }
    }
    
    // Phương thức xác thực chữ ký từ VNPay
    private boolean validateVNPaySignature(Map<String, String> vnpParams, String vnpSecureHash) {
        try {
            // Loại bỏ vnp_SecureHash khỏi danh sách parameters
            Map<String, String> paramsToValidate = new HashMap<>(vnpParams);
            paramsToValidate.remove("vnp_SecureHash");
            
            // Sắp xếp parameters theo thứ tự alphabet
            List<String> fieldNames = new ArrayList<>(paramsToValidate.keySet());
            Collections.sort(fieldNames);
            
            // Tạo chuỗi hash data
            StringBuilder hashData = new StringBuilder();
            for (String fieldName : fieldNames) {
                String fieldValue = paramsToValidate.get(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    hashData.append('&');
                }
            }
            
            // Xóa ký tự '&' cuối cùng
            if (hashData.length() > 0) {
                hashData.setLength(hashData.length() - 1);
            }
            
            // Tạo secure hash và so sánh
            String calculatedHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
            
            System.out.println("🔐 Hash validation:");
            System.out.println("Received hash: " + vnpSecureHash);
            System.out.println("Calculated hash: " + calculatedHash);
            
            return calculatedHash.equalsIgnoreCase(vnpSecureHash);
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xác thực chữ ký: " + e.getMessage());
            return false;
        }
    }
    
    // Phương thức chuyển đổi mã lỗi VNPay thành thông báo
    private String getVNPayErrorMessage(String responseCode) {
        switch (responseCode) {
            case "00": return "Giao dịch thành công";
            case "07": return "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường)";
            case "09": return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng";
            case "10": return "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11": return "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch";
            case "12": return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa";
            case "13": return "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP)";
            case "24": return "Giao dịch không thành công do: Khách hàng hủy giao dịch";
            case "51": return "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch";
            case "65": return "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày";
            case "75": return "Ngân hàng thanh toán đang bảo trì";
            case "79": return "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định";
            case "99": return "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)";
            default: return "Lỗi không xác định: " + responseCode;
        }
    }
}