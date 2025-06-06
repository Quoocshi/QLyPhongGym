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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hahaha.enums.LoaiDichVu;
import hahaha.model.BoMon;
import hahaha.model.DichVu;
import hahaha.service.DichVuService;

@Controller
@RequestMapping("/quan-ly-dich-vu")
public class QlyDichVuController {
    
    @Autowired
    private DichVuService dichVuService;

    @GetMapping("/danh-sach-dich-vu")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public String hienThiQuanLyDichVu(Authentication auth, Model model) {
        List<DichVu> dichVuList = dichVuService.getAll();
        model.addAttribute("dichVuList", dichVuList);
        return getViewByRole(auth, "qlydv");
    }

    @GetMapping("/them-dich-vu")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public String themDichVuForm(Authentication auth, Model model) {
        model.addAttribute("dichVu", new DichVu());
        model.addAttribute("nextMaDV", dichVuService.generateNextMaDV());
        
        List<BoMon> boMonList = dichVuService.getAllBoMon();
        model.addAttribute("boMonList", boMonList);
        
        return getViewByRole(auth, "add");
    }

    @PostMapping("/them-dich-vu")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public String themDichVu(@RequestParam("maDV") String maDV,
                            @RequestParam("tenDV") String tenDV,
                            @RequestParam("loaiDV") String loaiDV,
                            @RequestParam("thoiHan") int thoiHan,
                            @RequestParam("donGia") Double donGia,
                            @RequestParam("maBM") String maBM,
                            RedirectAttributes redirectAttributes) {
        try {
            DichVu dichVu = new DichVu();
            dichVu.setMaDV(maDV);
            dichVu.setTenDV(tenDV);
            dichVu.setLoaiDV(LoaiDichVu.valueOf(loaiDV));
            dichVu.setThoiHan(thoiHan);
            dichVu.setDonGia(donGia);
            
            // Set bộ môn cho dịch vụ
            BoMon boMon = new BoMon();
            boMon.setMaBM(maBM);
            dichVu.setBoMon(boMon);
            
            Boolean result = dichVuService.createDichVu(dichVu);
            if (result) {
                redirectAttributes.addFlashAttribute("successMessage", "Thêm dịch vụ thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi thêm dịch vụ!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/quan-ly-dich-vu/danh-sach-dich-vu";
    }

    @GetMapping("/cap-nhat-dich-vu/{maDV}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public String capNhatDichVuForm(@PathVariable String maDV, Authentication auth, Model model) {
        DichVu dichVu = dichVuService.findById(maDV);
        if (dichVu == null) {
            model.addAttribute("errorMessage", "Không tìm thấy dịch vụ!");
            return "redirect:/quan-ly-dich-vu/danh-sach-dich-vu";
        }
        
        model.addAttribute("dichVu", dichVu);
        
        List<BoMon> boMonList = dichVuService.getAllBoMon();
        model.addAttribute("boMonList", boMonList);
        
        return getViewByRole(auth, "update");
    }
    
    @PostMapping("/cap-nhat-dich-vu/{maDV}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public String capNhatDichVu(@PathVariable String maDV,
                               @RequestParam("tenDV") String tenDV,
                               @RequestParam("loaiDV") String loaiDV,
                               @RequestParam("thoiHan") int thoiHan,
                               @RequestParam("donGia") Double donGia,
                               @RequestParam("maBM") String maBM,
                               @RequestParam("version") int version,
                               RedirectAttributes redirectAttributes) {
        try {
            DichVu dichVu = dichVuService.findById(maDV);
            if (dichVu == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy dịch vụ!");
                return "redirect:/quan-ly-dich-vu/danh-sach-dich-vu";
            }
            
            dichVu.setTenDV(tenDV);
            dichVu.setLoaiDV(LoaiDichVu.valueOf(loaiDV));
            dichVu.setThoiHan(thoiHan);
            dichVu.setDonGia(donGia);
            dichVu.setVersion(version);
            
            // Set bộ môn cho dịch vụ
            BoMon boMon = new BoMon();
            boMon.setMaBM(maBM);
            dichVu.setBoMon(boMon);
            
            Boolean result = dichVuService.updateDichVu(dichVu);
            if (result) {
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật dịch vụ thành công!");
            }
        } catch (RuntimeException re) {
            redirectAttributes.addFlashAttribute("errorMessage", re.getMessage());
        }
         catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/quan-ly-dich-vu/danh-sach-dich-vu";
    }

    @PostMapping("/xoa-dich-vu")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public String xoaDichVu(@RequestParam String maDV, RedirectAttributes redirectAttributes) {
        try {
            Boolean result = dichVuService.deleteDichVu(maDV);
            if (result) {
                redirectAttributes.addFlashAttribute("successMessage", "Xóa dịch vụ thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa dịch vụ này!");
            }
        } catch (Exception e) {
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("constraint")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa dịch vụ này vì đã có khách hàng đăng ký!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa dịch vụ: " + e.getMessage());
            }
        }
        
        return "redirect:/quan-ly-dich-vu/danh-sach-dich-vu";
    }

    // Helper method để check role
    private String getViewByRole(Authentication auth, String viewName) {
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return isAdmin ? "Admin/DichVu/" + viewName : "Staff/DichVu/" + viewName;
    }
} 