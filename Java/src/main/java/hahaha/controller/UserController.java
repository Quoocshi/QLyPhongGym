package hahaha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hahaha.model.Account;
import hahaha.model.KhachHang;
import hahaha.repository.AccountRepository;
import hahaha.repository.KhachHangRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private KhachHangRepository khachHangRepository;
    
    @GetMapping("/home/{id}/{username}")
    public String homePage(Model model, @PathVariable Long id,@PathVariable String username) {
        Account acc = accountRepository.findByAccountId(id);
        String hoTen = (acc != null && acc.getKhachHang() != null) ? acc.getKhachHang().getHoTen() : "";
        model.addAttribute("accountId", id);
        model.addAttribute("username", username);
        model.addAttribute("hoTen", hoTen);
        return "User/home"; 
    }
    
    @GetMapping("/taikhoan/{id}")
    public String taiKhoanPage(Model model, @PathVariable Long id) {
        Account acc = accountRepository.findByAccountId(id);
        if (acc == null || acc.getKhachHang() == null) {
            return "redirect:/login";
        }
        
        KhachHang khachHang = acc.getKhachHang();
        model.addAttribute("account", acc);
        model.addAttribute("khachHang", khachHang);
        model.addAttribute("accountId", id);
        model.addAttribute("username", acc.getUserName());
        
        return "User/taikhoan";
    }
    
    @PostMapping("/taikhoan/{id}/update")
    public String updateTaiKhoan(@PathVariable Long id,
                                @RequestParam("hoTen") String hoTen,
                                @RequestParam("email") String email,
                                @RequestParam("soDienThoai") String soDienThoai,
                                @RequestParam("diaChi") String diaChi,
                                @RequestParam("gioiTinh") String gioiTinh,
                                @RequestParam("ngaySinh") String ngaySinhStr,
                                RedirectAttributes redirectAttributes) {
        try {
            Account acc = accountRepository.findByAccountId(id);
            if (acc == null || acc.getKhachHang() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin tài khoản!");
                return "redirect:/user/taikhoan/" + id;
            }
            
            KhachHang khachHang = acc.getKhachHang();
            
            // Cập nhật thông tin
            khachHang.setHoTen(hoTen);
            khachHang.setEmail(email);
            khachHang.setSoDienThoai(soDienThoai);
            khachHang.setDiaChi(diaChi);
            khachHang.setGioiTinh(gioiTinh);
            
            // Chuyển đổi ngày sinh
            if (ngaySinhStr != null && !ngaySinhStr.trim().isEmpty()) {
                LocalDate ngaySinh = LocalDate.parse(ngaySinhStr);
                khachHang.setNgaySinh(ngaySinh);
            }
            
            // Lưu thông tin
            khachHangRepository.save(khachHang);
            
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật thông tin: " + e.getMessage());
        }
        
        return "redirect:/user/taikhoan/" + id;
    }
}
