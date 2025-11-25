package hahaha.controller;

import hahaha.model.Account;
import hahaha.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/trainer")
@RequiredArgsConstructor
public class TrainerController {

    private final AccountRepository accountRepository;

    @GetMapping("/home")
    public ResponseEntity<?> getHomeInfo(Authentication authentication) {
        String username = authentication.getName(); // lấy username từ token
        Account acc = accountRepository.findAccountByUserName(username);

        if (acc == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy trainer với username = " + username);
        }

        // Ví dụ lấy thêm họ tên hoặc các thông tin khác nếu có
        String hoTen = acc.getNhanVien().getTenNV();

        Map<String, Object> res = new HashMap<>();
        res.put("trainerId", acc.getNhanVien().getMaNV());
        res.put("username", acc.getUserName());
        res.put("hoTen", hoTen);

        return ResponseEntity.ok(res);
    }
}
