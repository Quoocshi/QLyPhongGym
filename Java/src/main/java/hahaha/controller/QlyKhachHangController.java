package hahaha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff/customer")
public class QlyKhachHangController {

    @GetMapping("/qlycus")
    public String hienThiQuanLyKhachHang() {
        // Trả về view Staff/Customer/qlycus.html
        return "Staff/Customer/qlycus";
    }
}
