package hahaha.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "home";
    }

    @GetMapping("/admin")
    public String adminPage(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "admin"; 
    }
}
