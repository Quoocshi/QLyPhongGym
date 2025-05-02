package hahaha.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/home")
    public String homePage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "home"; 
    }
}
