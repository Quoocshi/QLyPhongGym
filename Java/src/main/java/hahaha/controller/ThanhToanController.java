package hahaha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import hahaha.model.HoaDon;
import hahaha.service.HoaDonService;

@Controller
@RequestMapping("/thanh-toan")
public class ThanhToanController {

    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping("/{maHD}")
    @PreAuthorize("hasRole('USER')")
    public String hienThiThanhToan(@PathVariable String maHD, Model model) {
        try {
            HoaDon hoaDon = hoaDonService.timMaHD(maHD);
            model.addAttribute("hoaDon", hoaDon);
            model.addAttribute("dsChiTiet", hoaDon.getDsChiTiet());
            return "User/thanhtoan"; 
        } catch (Exception e) {
            // Nếu không tìm thấy hóa đơn, redirect về trang đăng ký dịch vụ
            model.addAttribute("error", "Không tìm thấy hóa đơn: " + maHD);
            return "redirect:/dich-vu-gym/dang-kydv";
        }
    }

    @PostMapping("/{maHD}")
    @PreAuthorize("hasRole('USER')")
    public String thucHienThanhToan(@PathVariable String maHD) {
        try {
            hoaDonService.thanhToan(maHD);
            return "redirect:/thanh-toan/" + maHD + "?success=true";
        } catch (Exception e) {
            return "redirect:/thanh-toan/" + maHD + "?error=true";
        }
    }
}

