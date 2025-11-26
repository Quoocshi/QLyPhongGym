package hahaha.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import hahaha.config.Security.JwtIssuer;
import hahaha.model.Account;
import hahaha.model.LoginRequest;
import hahaha.model.RegisterRequest;
import hahaha.repository.AccountRepository;
import hahaha.service.AccountService;
import hahaha.service.AuthService.RegisterService;
import hahaha.service.GoogleService;
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

    @Autowired
    private JwtIssuer jwtIssuer;

    @Autowired
    private GoogleService googleService;

    @Autowired
    private AccountService accountService;
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User principal = (User) authentication.getPrincipal();
        Long accountId = accountService.getAccountIdByUsername(principal.getUsername());
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        String token = jwtIssuer.issue(
                accountId,
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
        Long accountId = accountService.getAccountIdByUsername(username);
        String token = jwtIssuer.issue(accountId,username, List.of("USER"));

        return ResponseEntity.ok(Map.of(
                "message", "Đăng ký thành công",
                "accessToken", token
        ));

    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> body) {
        try {
            String idToken = body.get("idToken");
            if (idToken == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu idToken"));
            }

            // Xác thực token Google
            GoogleIdToken.Payload payload = googleService.verifyIdToken(idToken);
            if (payload == null) {
                return ResponseEntity.status(401).body(Map.of("error", "ID Token không hợp lệ"));
            }

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // Kiểm tra user đã tồn tại chưa
            Account acc = accountService.findByEmail(email);

            if (acc == null) {
                // Nếu chưa có → tự động đăng ký tài khoản bằng email Google
                acc = accountService.registerGoogleUser(email, name);
            }

            Long accountId = acc.getAccountId();
            List<String> roles = List.of(acc.getRoleGroup().getNameRoleGroup());

            String token = jwtIssuer.issue(
                    accountId,
                    acc.getUserName(),
                    roles
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Đăng nhập bằng Google thành công",
                    "email", email,
                    "access_token", token
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


}


