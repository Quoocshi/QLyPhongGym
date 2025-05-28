package hahaha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hahaha.model.KhachHang;
import hahaha.service.KhachHangService;

@Controller
@RequestMapping("/quan-ly-khach-hang")
public class QlyKhachHangController {
    
    @Autowired
    private KhachHangService khachHangService;

    @GetMapping("/danh-sach-khach-hang")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public String hienThiQuanLyKhachHang(Authentication auth, Model model) {
        List<KhachHang> customers = khachHangService.getAll();
        model.addAttribute("customers", customers);
        return getViewByRole(auth, "qlycus");
    }

    @GetMapping("/cap-nhat-thong-tin-khach-hang/{maKH}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public String updateCustomerForm(@PathVariable String maKH, Authentication auth, Model model) {
        KhachHang customer = khachHangService.findById(maKH);
        model.addAttribute("customer", customer);
        return getViewByRole(auth, "update");
    }
    
    @PostMapping("/cap-nhat-thong-tin-khach-hang/{maKH}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public String updateCustomer(KhachHang customer) {
        khachHangService.updateCustomer(customer);
        return "redirect:/quan-ly-khach-hang/danh-sach-khach-hang";
    }

    @PostMapping("/xoa-khach-hang")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public String deleteCustomer(@RequestParam String MaKH) {
        khachHangService.deleteCustomer(MaKH);
        return "redirect:/quan-ly-khach-hang/danh-sach-khach-hang";
    }

    // Helper method để check role
    private String getViewByRole(Authentication auth, String viewName) {
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return isAdmin ? "Admin/Customer/" + viewName : "Staff/Customer/" + viewName;
    }
}