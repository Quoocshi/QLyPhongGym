package hahaha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QlyKhachHangController {

    @GetMapping("/staff/customer/qlycus")
    public String hienThiQuanLyKhachHangStaff() {
        // Trả về view Staff/Customer/qlycus.html
        return "Staff/Customer/qlycus";
    }

    @GetMapping("/admin/customer/qlycus")
    public String hienThiQuanLyKhachHangAdmin() {
        // Trả về view Admin/Customer/qlycus.html
        return "Admin/Customer/qlycus";
    }
}
