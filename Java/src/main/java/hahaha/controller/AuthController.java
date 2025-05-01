package hahaha.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {

    @GetMapping("/home/{username}")
    public Map<String, Object> homePage(@PathVariable String username, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("page", "home");
        response.put("loginUsername", principal.getName());
        response.put("pageUsername", username);
        if (!principal.getName().equals(username)) {
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập trang này.");
}

        return response;
    }

    @GetMapping("/admin/{username}")
    public Map<String, Object> adminPage(@PathVariable String username, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("page", "admin");
        response.put("loginUsername", principal.getName());
        response.put("pageUsername", username);
        if (!principal.getName().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập trang này.");
        }
        
        return response;
    }
}

