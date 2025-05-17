package hahaha.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import hahaha.model.Account;
import hahaha.model.RoleGroup;
import hahaha.repository.AccountRepository;

@Controller
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showRegisterPage(Model model) {
        model.addAttribute("account", new Account());
        return "reg";
    }

    @PostMapping
    public String register(@ModelAttribute("account") Account account, Model model) {
        try {
            System.out.println(">>> Đăng ký với username: " + account.getUserName());

            if (accountRepository.findAccountByUserName(account.getUserName()) != null) {
                model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
                model.addAttribute("showRegisterModal", true);
                return "reg";
            }

            if (accountRepository.existsByEmail(account.getEmail())) {
                model.addAttribute("errorMessage", "Email đã tồn tại.");
                model.addAttribute("showRegisterModal", true);
                return "reg";
            }
            RoleGroup roleGroup = new RoleGroup();
            roleGroup.setRoleGroupId(2L);
            account.setRoleGroup(roleGroup);
            account.setEmail(account.getEmail());
            account.setFullName(account.getFullName());
            account.setUserName(account.getUserName());
            account.setCreatedAt(LocalDateTime.now());
            account.setUpdatedAt(LocalDateTime.now());
            account.setPasswordHash(passwordEncoder.encode(account.getPasswordHash()));
            account.setIsDeleted(0);
            account.setStatus("ACTIVE");
            accountRepository.save(account);

            return "redirect:/login?registered=true";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Đã xảy ra lỗi khi đăng ký.");
            return "reg";
        }
    }
}
