package hahaha.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/")
    public String loginPage() {
        return "login"; // login.html
    }

    @GetMapping("/home")
    public String homePage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "home"; // home.html
    }

    @GetMapping("/admin")
    public String adminPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "admin"; // admin.html
    }
}


