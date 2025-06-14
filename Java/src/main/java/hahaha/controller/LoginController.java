package hahaha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping()
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "registered", required = false) String registered,
                           Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Sai username hoặc mật khẩu !");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Đăng xuất thành công!");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
        }
        return "log"; 
    }
}


