package hahaha.controller;

import hahaha.DTO.ChiTietKhachHangDTO;
import hahaha.DTO.LichTapDTO;

import hahaha.DTO.UpdateTaiKhoanRequest;
import hahaha.model.Account;
import hahaha.model.KhachHang;
import hahaha.model.LichTap;
import hahaha.repository.AccountRepository;
import hahaha.repository.KhachHangRepository;
import hahaha.service.LichTapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor   // dùng constructor injection thay cho @Autowired
public class UserController {

    private final AccountRepository accountRepository;
    private final KhachHangRepository khachHangRepository;
    private final LichTapService lichTapService;

    @GetMapping("/home")
    public ResponseEntity<?> getHomeInfo(Authentication authentication) {
        String username = authentication.getName();
        Account acc = accountRepository.findAccountByUserName(username);
        Long id =  acc.getAccountId();
        if (acc == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy account với id = " + id);
        }

        String hoTen = (acc.getKhachHang() != null) ? acc.getKhachHang().getHoTen() : "";

        Map<String, Object> res = new HashMap<>();
        res.put("accountId", id);
        res.put("username", acc.getUserName());
        res.put("hoTen", hoTen);

        return ResponseEntity.ok(res);
    }

    @GetMapping("/lich-tap")
    public ResponseEntity<?> getLichTap(Authentication authentication) {
        try {
            Account acc = accountRepository.findAccountByUserName(authentication.getName());
            Long id  = acc.getAccountId();
            if (acc == null || acc.getKhachHang() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy tài khoản hoặc khách hàng tương ứng");
            }

            KhachHang khachHang = acc.getKhachHang();
            String maKH = khachHang.getMaKH();

            List<LichTap> danhSachLichTap = lichTapService.getAllLichTapByKhachHang(maKH);

            List<LichTapDTO> danhSachLichTapDTO = new ArrayList<>();
            if (danhSachLichTap != null) {
                for (LichTap lichTap : danhSachLichTap) {
                    danhSachLichTapDTO.add(new LichTapDTO(lichTap));
                }
            }

            Map<String, Object> res = new HashMap<>();
            res.put("accountId", id);
            res.put("username", acc.getUserName());
            //res.put("khachHang", new ChiTietKhachHangDTO(khachHang));
            res.put("danhSachLichTap", danhSachLichTapDTO);

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @GetMapping("/taikhoan")
    public ResponseEntity<?> getTaiKhoan(Authentication authentication) {
        Account acc = accountRepository.findAccountByUserName(authentication.getName());
        Long id = acc.getAccountId();
        if (acc == null || acc.getKhachHang() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy thông tin tài khoản!");
        }

        KhachHang khachHang = acc.getKhachHang();

        Map<String, Object> res = new HashMap<>();
        res.put("accountId", id);
        res.put("username", acc.getUserName());
        res.put("khachHang", new ChiTietKhachHangDTO(khachHang));

        return ResponseEntity.ok(res);
    }

    @PutMapping("/taikhoan")
    public ResponseEntity<?> updateTaiKhoan(
            @RequestBody UpdateTaiKhoanRequest request,
            Authentication authentication
            ) {

        try {
            Account acc = accountRepository.findAccountByUserName(authentication.getName());
            Long id = acc.getAccountId();
            if (acc == null || acc.getKhachHang() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy thông tin tài khoản!");
            }

            KhachHang khachHang = acc.getKhachHang();

            khachHang.setHoTen(request.getHoTen());
            khachHang.setEmail(request.getEmail());
            khachHang.setSoDienThoai(request.getSoDienThoai());
            khachHang.setDiaChi(request.getDiaChi());
            khachHang.setGioiTinh(request.getGioiTinh());

            if (request.getNgaySinh() != null && !request.getNgaySinh().trim().isEmpty()) {
                LocalDate ngaySinh = LocalDate.parse(request.getNgaySinh());
                khachHang.setNgaySinh(ngaySinh);
            }

            khachHangRepository.save(khachHang);

            return ResponseEntity.ok(new ChiTietKhachHangDTO(khachHang));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật thông tin: " + e.getMessage());
        }
    }
}