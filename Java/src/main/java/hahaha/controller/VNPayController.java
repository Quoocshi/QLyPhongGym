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
        Map<String, String> vnpParams = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> vnpParams.put(key, values[0]));

        String maHD = vnpParams.get("vnp_OrderInfo").replace("Thanh toan don hang:", "");
        hoaDonService.thanhToan(maHD);
        
        return "redirect:/thanh-toan/" + maHD + "?success=true" ;
    }
}