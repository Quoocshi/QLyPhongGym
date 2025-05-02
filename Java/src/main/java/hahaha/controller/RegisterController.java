package hahaha.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hahaha.model.Account;
import hahaha.model.AccountAssignRoleGroup;
import hahaha.model.AccountAssignRoleGroupId;
import hahaha.model.UserGym;
import hahaha.repository.AccountAssignRoleGroupRepository;
import hahaha.repository.AccountRepository;
import hahaha.repository.UserGymRepository;

@Controller
@RequestMapping("/register")

public class RegisterController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountAssignRoleGroupRepository assignRoleGroupRepository;
    
    @Autowired
    private UserGymRepository userRepository;

@PostMapping
public String register(@ModelAttribute("account") Account account, Model model,
                       @RequestParam("fullName") String fullName,
                       @RequestParam("email") String email) {
    try {
        System.out.println(">>> Đăng ký với username: " + account.getUsername());

        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
            return "login";
        }

        if (userRepository.existsByEmail(email)) {
            model.addAttribute("errorMessage", "Email đã tồn tại.");
            return "login";
        }

        // Bước 1: Tạo USER
        UserGym user = new UserGym();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsDeleted(0);
        userRepository.save(user);

        // Bước 2: Tạo ACCOUNT gán với USER_ID
        account.setUserId(user.getUserId());
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setPasswordHash(passwordEncoder.encode(account.getPasswordHash()));
        account.setIsDeleted(0);
        account.setStatus("ACTIVE");
        Account saved = accountRepository.save(account);

        // Bước 3: Gán quyền mặc định
        AccountAssignRoleGroup roleMap = new AccountAssignRoleGroup();
        roleMap.setId(new AccountAssignRoleGroupId(saved.getAccountId(), 2L)); // 2L là ROLE_USER
        roleMap.setCreatedAt(LocalDateTime.now());
        roleMap.setUpdatedAt(LocalDateTime.now());
        roleMap.setIsDeleted(0);
        assignRoleGroupRepository.save(roleMap);

        return "redirect:/login?registered=true";

    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("errorMessage", "Đã xảy ra lỗi khi đăng ký.");
        return "login";
    }
}

    
}
