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
import hahaha.model.LichTap;
import hahaha.repository.AccountRepository;
import hahaha.repository.KhachHangRepository;
import hahaha.service.LichTapService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private KhachHangRepository khachHangRepository;
    
    @Autowired
    private LichTapService lichTapService;
    
    // DTO class để tránh circular reference
    public static class LichTapDTO {
        private String maLT;
        private String loaiLich;
        private String thu;
        private String trangThai;
        private String tenCaTap;
        private String moTaCaTap;
        private String tenNhanVien;
        private String tenLop;
        private String tenKhuVuc;
        
        // Constructors
        public LichTapDTO() {}
        
        public LichTapDTO(LichTap lichTap) {
            this.maLT = lichTap.getMaLT();
            this.loaiLich = lichTap.getLoaiLich();
            this.thu = lichTap.getThu();
            this.trangThai = lichTap.getTrangThai();
            
            if (lichTap.getCaTap() != null) {
                this.tenCaTap = lichTap.getCaTap().getTenCa();
                this.moTaCaTap = lichTap.getCaTap().getMoTa();
            }
            
            if (lichTap.getNhanVien() != null) {
                this.tenNhanVien = lichTap.getNhanVien().getTenNV();
            }
            
            if (lichTap.getLop() != null) {
                this.tenLop = lichTap.getLop().getTenLop();
            }
            
            if (lichTap.getKhuVuc() != null) {
                this.tenKhuVuc = lichTap.getKhuVuc().getTenKhuVuc();
            }
        }
        
        // Getters and Setters
        public String getMaLT() { return maLT; }
        public void setMaLT(String maLT) { this.maLT = maLT; }
        
        public String getLoaiLich() { return loaiLich; }
        public void setLoaiLich(String loaiLich) { this.loaiLich = loaiLich; }
        
        public String getThu() { return thu; }
        public void setThu(String thu) { this.thu = thu; }
        
        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
        
        public String getTenCaTap() { return tenCaTap; }
        public void setTenCaTap(String tenCaTap) { this.tenCaTap = tenCaTap; }
        
        public String getMoTaCaTap() { return moTaCaTap; }
        public void setMoTaCaTap(String moTaCaTap) { this.moTaCaTap = moTaCaTap; }
        
        public String getTenNhanVien() { return tenNhanVien; }
        public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
        
        public String getTenLop() { return tenLop; }
        public void setTenLop(String tenLop) { this.tenLop = tenLop; }
        
        public String getTenKhuVuc() { return tenKhuVuc; }
        public void setTenKhuVuc(String tenKhuVuc) { this.tenKhuVuc = tenKhuVuc; }
    }
    
    @GetMapping("/home/{id}/{username}")
    public String homePage(Model model, @PathVariable Long id,@PathVariable String username) {
        Account acc = accountRepository.findByAccountId(id);
        String hoTen = (acc != null && acc.getKhachHang() != null) ? acc.getKhachHang().getHoTen() : "";
        model.addAttribute("accountId", id);
        model.addAttribute("username", username);
        model.addAttribute("hoTen", hoTen);
        return "User/home"; 
    }
    
    @GetMapping("/lich-tap/{id}")
    public String lichTapPage(Model model, @PathVariable Long id) {
        try {
            System.out.println("=== DEBUG User LichTap ===");
            System.out.println("AccountId: " + id);
            
            Account acc = accountRepository.findByAccountId(id);
            if (acc == null || acc.getKhachHang() == null) {
                System.out.println("❌ Account not found or not a customer");
                return "redirect:/login";
            }
            
            KhachHang khachHang = acc.getKhachHang();
            String maKH = khachHang.getMaKH();
            
            System.out.println("Customer: " + khachHang.getHoTen() + " (" + maKH + ")");
            
            // Lấy tất cả lịch tập của khách hàng
            List<LichTap> danhSachLichTap = lichTapService.getAllLichTapByKhachHang(maKH);
            System.out.println("Số lịch tập: " + (danhSachLichTap != null ? danhSachLichTap.size() : "null"));
            
            // Chuyển đổi sang DTO để tránh circular reference
            List<LichTapDTO> danhSachLichTapDTO = new ArrayList<>();
            if (danhSachLichTap != null) {
                for (LichTap lichTap : danhSachLichTap) {
                    System.out.println("- Lịch tập: " + lichTap.getMaLT() + " | " + lichTap.getLoaiLich() + " | " + lichTap.getThu());
                    danhSachLichTapDTO.add(new LichTapDTO(lichTap));
                }
            }
            
            model.addAttribute("accountId", id);
            model.addAttribute("username", acc.getUserName());
            model.addAttribute("khachHang", khachHang);
            model.addAttribute("danhSachLichTap", danhSachLichTapDTO);
            
            System.out.println("✅ Trả về template: User/lichtap");
            return "User/lichtap";
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi trong lichTapPage: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "User/lichtap";
        }
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
