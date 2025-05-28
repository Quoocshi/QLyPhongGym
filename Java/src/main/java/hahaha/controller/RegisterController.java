package hahaha.controller;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import hahaha.model.Account;
import hahaha.model.KhachHang;
import hahaha.model.RegisterForm;
import hahaha.model.RoleGroup;
import hahaha.repository.AccountRepository;
import hahaha.repository.KhachHangRepository;
import hahaha.service.KhachHangService;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KhachHangService khachHangService;

    @GetMapping()
    public String showRegisterPage(Model model) {
        RegisterForm form = new RegisterForm();
        //form.setHoTen("User2");
        // form.setDiaChi("20/03/2005");
        // form.setEmail("user1@example.com");
        // form.setGioiTinh("Nam");
        // form.setNgaySinh(java.time.LocalDate.parse("2005-03-20"));
        // form.setUserName("User1");
        // form.setPassword("111");
        // form.setSoDienThoai("08716736111");
        model.addAttribute("registerForm", form);
        return "reg";
    }

    @PostMapping()
    public String register(@ModelAttribute("registerForm") RegisterForm form, Model model) {
        try {
            if (accountRepository.findAccountByUserName(form.getUserName()) != null) {
                model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
                return "reg";
            }

            if (accountRepository.existsByEmail(form.getEmail())) {
                model.addAttribute("errorMessage", "Email đã tồn tại.");
                return "reg";
            }

            // Tạo khách hàng
            KhachHang kh = new KhachHang();
            kh.setMaKH(khachHangService.generateNextMaKH()); 
            kh.setHoTen(form.getHoTen());
            kh.setGioiTinh(form.getGioiTinh());
            kh.setNgaySinh(form.getNgaySinh());
            kh.setEmail(form.getEmail());
            kh.setSoDienThoai(form.getSoDienThoai());
            kh.setDiaChi(form.getDiaChi());
            kh.setReferralCode(khachHangService.generateNextReferralCode());
            khachHangRepository.save(kh);

            // Tạo account
            Account account = new Account();
            account.setUserName(form.getUserName());
            account.setPasswordHash(passwordEncoder.encode(form.getPassword()));
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            account.setStatus("ACTIVE");
            account.setIsDeleted(0);
            account.setKhachHang(kh);

            RoleGroup roleGroup = new RoleGroup();
            roleGroup.setRoleGroupId(3L); // ROLE_USER
            account.setRoleGroup(roleGroup);
            
            accountRepository.save(account);

            return "redirect:/login?registered=true";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Đã xảy ra lỗi khi đăng ký.");
            return "reg";
        }
    }
}
