package hahaha.controller;

import hahaha.config.Security.JwtIssuer;
import hahaha.model.CustomUserDetails;
import hahaha.model.LoginRequest;
import hahaha.model.RegisterRequest;
import hahaha.service.AuthService.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RegisterService registerService;

    private final JwtIssuer jwtIssuer;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User principal = (User) authentication.getPrincipal();
//        String userId = principal.getUserId().toString();
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        String token = jwtIssuer.issue(
                principal.getUsername(),
                roles
        );
        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "Đăng nhập thành công");
        response.put("username", principal.getUsername());
        response.put("access_token", token);

        return ResponseEntity.ok(response);

        }
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> result = registerService.register(request);

        String username = result.get("username").toString();
        String token = jwtIssuer.issue(username, List.of("USER"));

        return ResponseEntity.ok(Map.of(
                "message", "Đăng ký thành công",
                "accessToken", token
        ));

    }

}


