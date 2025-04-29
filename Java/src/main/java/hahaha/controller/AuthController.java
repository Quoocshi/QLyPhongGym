package hahaha.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    // @GetMapping("/login")
    // public Map<String, String> loginPage() {
    //     Map<String, String> response = new HashMap<>();
    //     response.put("message", "Bạn cần đăng nhập.");
    //     return response;
    // }

    @GetMapping("/home")
    public Map<String, Object> home(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("page", "home");
        response.put("username", principal.getName());
        return response;
    }

    @GetMapping("/admin")
    public Map<String, Object> adminPage(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("page", "admin");
        response.put("username", principal.getName());
        return response;
    }
}
