package hahaha.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String hienThiThanhToan(@PathVariable String maHD, Model model) {
        HoaDon hoaDon = hoaDonService.timMaHD(maHD);
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("dsChiTiet", hoaDon.getDsChiTiet());
        return "User/thanhtoan"; 
    }

    @PostMapping("/{maHD}")
    public String thucHienThanhToan(@PathVariable String maHD) {
        hoaDonService.thanhToan(maHD);
        return "redirect:/thanh-toan/" + maHD + "?success=true";
    }
}

