package hahaha.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hahaha.enums.LoaiNhanVien;
import hahaha.model.Account;
import hahaha.model.NhanVien;
import hahaha.model.RoleGroup;
import hahaha.repository.AccountRepository;
import hahaha.repository.RoleGroupRepository;
import hahaha.service.NhanVienService;

@Controller
@RequestMapping("/quan-ly-nhan-vien")
public class QlyNhanVienController {
    
    @Autowired
    private NhanVienService nhanVienService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private RoleGroupRepository roleGroupRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/danh-sach-nhan-vien")
    @PreAuthorize("hasRole('ADMIN')")
    public String hienThiQuanLyNhanVien(Authentication auth, Model model) {
        try {
            // Chỉ lấy tất cả nhân viên, bỏ logic tìm kiếm backend
            List<NhanVien> nhanVienList = nhanVienService.getAll();
            model.addAttribute("nhanVienList", nhanVienList);
            return getViewByRole(auth, "qlynv");
            
        } catch (Exception e) {
            System.err.println("Lỗi trong controller quản lý nhân viên: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi tải danh sách nhân viên.");
            model.addAttribute("nhanVienList", nhanVienService.getAll());
            return getViewByRole(auth, "qlynv");
        }
    }

    @GetMapping("/them-nhan-vien")
    @PreAuthorize("hasRole('ADMIN')")
    public String themNhanVienForm(Authentication auth, Model model) {
        model.addAttribute("nhanVien", new NhanVien());
        model.addAttribute("nextMaNV", nhanVienService.generateNextMaNV());
        model.addAttribute("nextMaQL", nhanVienService.generateNextMaQL());
        model.addAttribute("nextMaPT", nhanVienService.generateNextMaPT());
        
        // Thêm danh sách loại nhân viên
        model.addAttribute("loaiNhanVienList", LoaiNhanVien.values());
        
        return getViewByRole(auth, "add");
    }

    @PostMapping("/them-nhan-vien")
    @PreAuthorize("hasRole('ADMIN')")
    public String themNhanVien(NhanVien nhanVien, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra email đã tồn tại
            if (accountRepository.existsByEmail(nhanVien.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã tồn tại trong hệ thống!");
                return "redirect:/quan-ly-nhan-vien/them-nhan-vien";
            }
            
            // Sử dụng email làm username trực tiếp
            String username = nhanVien.getEmail();
            
            // Kiểm tra username đã tồn tại
            if (accountRepository.findAccountByUserName(username) != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email này đã được sử dụng làm tài khoản đăng nhập!");
                return "redirect:/quan-ly-nhan-vien/them-nhan-vien";
            }
            
            // Password mặc định
            String password = "123456";
            
            Boolean resultNV = nhanVienService.createNhanVien(nhanVien);
            if (!resultNV) {
                redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi tạo nhân viên!");
                return "redirect:/quan-ly-nhan-vien/them-nhan-vien";
            }

            // Tạo tài khoản cho nhân viên
            Account account = new Account();
            account.setUserName(username);
            account.setPasswordHash(passwordEncoder.encode(password));
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            account.setStatus("ACTIVE");
            account.setIsDeleted(0);
            account.setNhanVien(nhanVien);

            // Gán role group dựa trên loại nhân viên
            Long roleGroupId = getRoleGroupIdByLoaiNV(nhanVien.getLoaiNV().name());
            RoleGroup roleGroup = new RoleGroup();
            roleGroup.setRoleGroupId(roleGroupId);
            account.setRoleGroup(roleGroup);
            
            accountRepository.save(account);
            
            redirectAttributes.addFlashAttribute("successMessage", "Thêm nhân viên và tạo tài khoản thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/quan-ly-nhan-vien/danh-sach-nhan-vien";
    }

    @GetMapping("/cap-nhat-nhan-vien/{maNV}")
    @PreAuthorize("hasRole('ADMIN')")
    public String capNhatNhanVienForm(@PathVariable String maNV, Authentication auth, Model model) {
        NhanVien nhanVien = nhanVienService.findById(maNV);
        if (nhanVien == null) {
            model.addAttribute("errorMessage", "Không tìm thấy nhân viên!");
            return "redirect:/quan-ly-nhan-vien/danh-sach-nhan-vien";
        }
        
        model.addAttribute("nhanVien", nhanVien);
        model.addAttribute("loaiNhanVienList", LoaiNhanVien.values());
        
        return getViewByRole(auth, "update");
    }
    
    @PostMapping("/cap-nhat-nhan-vien")
    @PreAuthorize("hasRole('ADMIN')")
    public String capNhatNhanVien(NhanVien nhanVienUpdate, RedirectAttributes redirectAttributes) {
        try {
            NhanVien nhanVien = nhanVienService.findById(nhanVienUpdate.getMaNV());
            if (nhanVien == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên!");
                return "redirect:/quan-ly-nhan-vien/danh-sach-nhan-vien";
            }
            
            // Kiểm tra email đã tồn tại cho nhân viên khác
            if (accountRepository.existsByEmail(nhanVienUpdate.getEmail()) && !nhanVien.getEmail().equals(nhanVienUpdate.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã tồn tại trong hệ thống!");
                return "redirect:/quan-ly-nhan-vien/cap-nhat-nhan-vien/" + nhanVienUpdate.getMaNV();
            }
            
            // Cập nhật thông tin nhân viên
            nhanVien.setTenNV(nhanVienUpdate.getTenNV());
            nhanVien.setNgaySinh(nhanVienUpdate.getNgaySinh());
            nhanVien.setGioiTinh(nhanVienUpdate.getGioiTinh());
            nhanVien.setEmail(nhanVienUpdate.getEmail());
            nhanVien.setNgayVaoLam(nhanVienUpdate.getNgayVaoLam());
            nhanVien.setLoaiNV(nhanVienUpdate.getLoaiNV());
            
            Boolean result = nhanVienService.updateNhanVien(nhanVien);
            
            // Cập nhật account (username và role) theo thông tin nhân viên mới
            Account account = accountRepository.findByNhanVien_MaNV(nhanVienUpdate.getMaNV());
            if (account != null) {
                // Cập nhật username nếu email thay đổi
                if (!nhanVien.getEmail().equals(nhanVienUpdate.getEmail())) {
                    // Kiểm tra email/username mới đã tồn tại chưa
                    if (accountRepository.findAccountByUserName(nhanVienUpdate.getEmail()) != null) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Email này đã được sử dụng làm tài khoản đăng nhập!");
                        return "redirect:/quan-ly-nhan-vien/cap-nhat-nhan-vien/" + nhanVienUpdate.getMaNV();
                    }
                    account.setUserName(nhanVienUpdate.getEmail());
                }
                
                // Cập nhật role
                Long roleGroupId = getRoleGroupIdByLoaiNV(nhanVienUpdate.getLoaiNV().name());
                RoleGroup roleGroup = new RoleGroup();
                roleGroup.setRoleGroupId(roleGroupId);
                account.setRoleGroup(roleGroup);
                account.setUpdatedAt(LocalDateTime.now());
                accountRepository.save(account);
            }
            
            if (result) {
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nhân viên và quyền hạn thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật nhân viên!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/quan-ly-nhan-vien/danh-sach-nhan-vien";
    }

    @PostMapping("/xoa-nhan-vien")
    @PreAuthorize("hasRole('ADMIN')")
    public String xoaNhanVien(@RequestParam String maNV, RedirectAttributes redirectAttributes) {
        try {
            Boolean result = nhanVienService.deleteNhanVien(maNV);
            if (result) {
                redirectAttributes.addFlashAttribute("successMessage", "Xóa nhân viên thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa nhân viên này!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("constraint")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa nhân viên này vì đã có dữ liệu liên quan!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa nhân viên: " + e.getMessage());
            }
        }
        
        return "redirect:/quan-ly-nhan-vien/danh-sach-nhan-vien";
    }

    // Helper method để check role
    private String getViewByRole(Authentication auth, String viewName) {
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return isAdmin ? "Admin/NhanVien/" + viewName : "Staff/NhanVien/" + viewName;
    }
    
    // Helper method để lấy role group ID dựa trên loại nhân viên
    private Long getRoleGroupIdByLoaiNV(String loaiNV) {
        return switch (loaiNV) {
            case "QuanLy" -> 1L; // ADMIN role
            case "LeTan" -> 2L;  // STAFF role
            case "Trainer" -> 4L; // TRAINER role
            case "PhongTap" -> 2L; // STAFF role
            default -> 2L; // Default to STAFF
        };
    }
} 