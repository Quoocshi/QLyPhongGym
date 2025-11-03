//package hahaha.controller;
//import java.time.LocalDateTime;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import hahaha.model.Account;
//import hahaha.model.KhachHang;
//import hahaha.model.RegisterForm;
//import hahaha.model.RoleGroup;
//import hahaha.repository.AccountRepository;
//import hahaha.repository.KhachHangRepository;
//import hahaha.service.KhachHangService;
//import jakarta.validation.Valid;
//
//@RestController
//@RequestMapping("/api/auth")
//public class RegisterController {
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private KhachHangRepository khachHangRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private KhachHangService khachHangService;
//
//
//    @PostMapping("/register")
//    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
//                          BindingResult bindingResult,
//                          Model model) {
//
//
//        // CLEAN UP DATA TRƯỚC KHI VALIDATE
//        if (form.getSoDienThoai() != null) {
//            String cleanPhone = form.getSoDienThoai().replaceAll("\\s+", "");
//            form.setSoDienThoai(cleanPhone);
//            System.out.println("Cleaned phone: '" + cleanPhone + "'");
//        }
//
//        // Validation tuổi trước tiên (luôn kiểm tra)
//        if (form.getNgaySinh() != null && !form.isValidAge()) {
//            int age = form.getAge();
//            if (age < 16) {
//                model.addAttribute("errorMessage", "Bạn phải từ 16 tuổi trở lên để đăng ký (hiện tại: " + age + " tuổi)!");
//                model.addAttribute("registerForm", form);
//                return "reg";
//            }
//            if (age > 100) {
//                model.addAttribute("errorMessage", "Tuổi không hợp lệ (" + age + " tuổi). Vui lòng kiểm tra lại ngày sinh!");
//                model.addAttribute("registerForm", form);
//                return "reg";
//            }
//        }
//
//        // MANUAL VALIDATION cho cleaned phone
//        if (form.getSoDienThoai() != null) {
//            String phone = form.getSoDienThoai();
//            if (!phone.matches("^(\\+84|0)[0-9]{9,10}$")) {
//                model.addAttribute("errorMessage", "Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có 10-11 chữ số (VD: 0901234567)");
//                model.addAttribute("registerForm", form);
//                return "reg";
//            }
//        }
//
//        // Kiểm tra validation errors từ annotation
//        if (bindingResult.hasErrors()) {
//            System.out.println("Validation errors found (" + bindingResult.getErrorCount() + " errors):");
//            bindingResult.getAllErrors().forEach(error ->
//                System.out.println("  - " + error.getDefaultMessage())
//            );
//            model.addAttribute("registerForm", form);
//            model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin đã nhập!");
//            return "reg";
//        }
//
//        try {
//            System.out.println("🔍 Checking business logic for user: " + form.getUserName());
//
//            // Phone đã được cleaned ở trên
//
//            // Kiểm tra tên đăng nhập đã tồn tại
//            if (accountRepository.findAccountByUserName(form.getUserName()) != null) {
//                System.out.println("Username already exists: " + form.getUserName());
//                model.addAttribute("errorMessage", "Tên đăng nhập '" + form.getUserName() + "' đã tồn tại. Vui lòng chọn tên khác!");
//                model.addAttribute("registerForm", form);
//                return "reg";
//            }
//
//            // Kiểm tra email đã tồn tại
//            if (accountRepository.existsByEmail(form.getEmail())) {
//                System.out.println("Email already exists: " + form.getEmail());
//                model.addAttribute("errorMessage", "Email '" + form.getEmail() + "' đã được đăng ký. Vui lòng sử dụng email khác!");
//                model.addAttribute("registerForm", form);
//                return "reg";
//            }
//
//            // Kiểm tra số điện thoại đã tồn tại (nếu có)
//            if (form.getSoDienThoai() != null && !form.getSoDienThoai().trim().isEmpty()) {
//                KhachHang existingCustomer = khachHangRepository.findBySoDienThoai(form.getSoDienThoai().trim());
//                if (existingCustomer != null) {
//                    System.out.println("Phone number already exists: " + form.getSoDienThoai());
//                    model.addAttribute("errorMessage", "Số điện thoại '" + form.getSoDienThoai() + "' đã được đăng ký. Vui lòng sử dụng số khác!");
//                    model.addAttribute("registerForm", form);
//                    return "reg";
//                }
//            }
//
//            // Age validation đã được kiểm tra ở trên
//
//            // Validation mật khẩu mạnh (bổ sung)
//            String password = form.getPassword();
//            if (password.length() < 6) {
//                model.addAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự!");
//                model.addAttribute("registerForm", form);
//                return "reg";
//            }
//            if (!password.matches(".*[a-z].*")) {
//                model.addAttribute("errorMessage", "Mật khẩu phải chứa ít nhất 1 chữ thường!");
//                model.addAttribute("registerForm", form);
//                return "reg";
//            }
//            if (!password.matches(".*[A-Z].*")) {
//                model.addAttribute("errorMessage", "Mật khẩu phải chứa ít nhất 1 chữ HOA!");
//                model.addAttribute("registerForm", form);
//                return "reg";
//            }
//            if (!password.matches(".*\\d.*")) {
//                model.addAttribute("errorMessage", "Mật khẩu phải chứa ít nhất 1 chữ số!");
//                model.addAttribute("registerForm", form);
//                return "reg";
//            }
//
//            System.out.println("All validations passed. Creating customer...");
//
//            // Tạo khách hàng
//            KhachHang kh = new KhachHang();
//            kh.setMaKH(khachHangService.generateNextMaKH());
//            kh.setHoTen(form.getHoTen().trim());
//            kh.setGioiTinh(form.getGioiTinh());
//            kh.setNgaySinh(form.getNgaySinh());
//            kh.setEmail(form.getEmail().toLowerCase().trim());
//            kh.setSoDienThoai(form.getSoDienThoai() != null ? form.getSoDienThoai().trim() : "");
//            kh.setDiaChi(form.getDiaChi() != null ? form.getDiaChi().trim() : "");
//            kh.setReferralCode(khachHangService.generateNextReferralCode());
//
//            try {
//                khachHangRepository.save(kh);
//                System.out.println("Customer created successfully: " + kh.getMaKH());
//            } catch (Exception e) {
//                System.out.println("Failed to create customer: " + e.getMessage());
//                model.addAttribute("errorMessage", "Lỗi khi tạo thông tin khách hàng. Vui lòng thử lại!");
//                model.addAttribute("registerForm", form);
//                return "reg";
//            }
//
//            // Tạo account
//            Account account = new Account();
//            account.setUserName(form.getUserName().trim());
//            account.setPasswordHash(passwordEncoder.encode(form.getPassword()));
//            account.setCreatedAt(LocalDateTime.now());
//            account.setUpdatedAt(LocalDateTime.now());
//            account.setStatus("ACTIVE");
//            account.setIsDeleted(0);
//            account.setKhachHang(kh);
//
//            RoleGroup roleGroup = new RoleGroup();
//            roleGroup.setRoleGroupId(3L); // ROLE_USER
//            account.setRoleGroup(roleGroup);
//
//            try {
//                accountRepository.save(account);
//                System.out.println("Account created successfully: " + account.getUserName());
//            } catch (Exception e) {
//                System.out.println("Failed to create account: " + e.getMessage());
//                // Rollback: xóa khách hàng đã tạo
//                try {
//                    khachHangRepository.delete(kh);
//                } catch (Exception rollbackException) {
//                    System.out.println("Rollback failed: " + rollbackException.getMessage());
//                }
//                model.addAttribute("errorMessage", "Lỗi khi tạo tài khoản đăng nhập. Vui lòng thử lại!");
//                model.addAttribute("registerForm", form);
//                return "reg";
//            }
//
//            System.out.println("Registration completed successfully for: " + form.getUserName());
//
//            // Thay vì redirect ngay, hiển thị thông báo thành công ở trang register
//            model.addAttribute("successMessage",
//                "Đăng ký thành công! Tài khoản '" + form.getUserName() + "' đã được tạo. " +
//                "Bạn sẽ được chuyển đến trang đăng nhập sau 3 giây...");
//            model.addAttribute("registerForm", new RegisterForm()); // Reset form
//            model.addAttribute("autoRedirect", true); // Flag để trigger auto-redirect
//            return "reg";
//
//        } catch (Exception e) {
//            System.out.println("Unexpected error during registration: " + e.getMessage());
//            e.printStackTrace();
//            model.addAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau hoặc liên hệ hỗ trợ!");
//            model.addAttribute("registerForm", form);
//            return "reg";
//        }
//    }
//}
//
