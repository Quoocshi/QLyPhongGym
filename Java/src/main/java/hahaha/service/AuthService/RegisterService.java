package hahaha.service.AuthService;

import hahaha.model.*;
import hahaha.repository.AccountRepository;
import hahaha.repository.KhachHangRepository;
import hahaha.service.KhachHangService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterService {

    private final AccountRepository accountRepository;
    private final KhachHangRepository khachHangRepository;
    private final KhachHangService khachHangService;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> register(@Valid RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();

        // --- Làm sạch dữ liệu ---
        request.setSoDienThoai(request.getSoDienThoai().replaceAll("\\s+", "").trim());
        request.setEmail(request.getEmail().toLowerCase().trim());
        request.setUsername(request.getUsername().trim());

        validateUserAge(request.getNgaySinh());
        validateUsername(request.getUsername());
        validateUsername(request.getUsername());
        validatePassword(request.getPassword());
        validatePhone(request.getSoDienThoai());
        // --- Tạo khách hàng ---
        KhachHang kh = createKhachHang(request);

        // --- Tạo tài khoản ---
        createAccount(request,kh);
        Account account = accountRepository.findAccountByUserName(request.getUsername());
        // --- Trả kết quả ---
        response.put("message", "Đăng ký thành công!");
        response.put("userId", kh.getMaKH());
        response.put("username", account.getUserName());
        response.put("role", "USER");
        response.put("createdAt", account.getCreatedAt().toString());
        return response;
    }

    private void validateUserAge(LocalDate dob){
        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 16) {
            throw new IllegalArgumentException("Bạn phải từ 16 tuổi trở lên để đăng ký! (hiện tại: " + age + ")");
        }
        if (age > 100) {
            throw new IllegalArgumentException("Tuổi không hợp lệ (" + age + "). Vui lòng kiểm tra lại ngày sinh!");
        }
    }

    private void validateUsername(String username){
        if (accountRepository.findAccountByUserName(username) != null) {
            throw new IllegalArgumentException("Tên đăng nhập '" + username + "' đã tồn tại!");
        }
    };
    private void validateEmail(String email){
        if (accountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email '" + email + "' đã được đăng ký!");
        }
    };
    public void validatePhone(String sdt){
        KhachHang existingCustomer = khachHangRepository.findBySoDienThoai(sdt);
        if (existingCustomer != null) {
            throw new IllegalArgumentException("Số điện thoại '" + sdt + "' đã được đăng ký!");
        }
    };
    public void validatePassword(String pass){
        if (pass.length() < 6)
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự!");
        if (!pass.matches(".*[a-z].*"))
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất 1 chữ thường!");
        if (!pass.matches(".*[A-Z].*"))
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất 1 chữ HOA!");
        if (!pass.matches(".*\\d.*"))
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất 1 chữ số!");
    };

    private KhachHang createKhachHang(RegisterRequest request){
        KhachHang kh = new KhachHang();
        kh.setMaKH(khachHangService.generateNextMaKH());
        kh.setHoTen(request.getHoTen());
        kh.setGioiTinh(request.getGioiTinh());
        kh.setNgaySinh(request.getNgaySinh());
        kh.setEmail(request.getEmail());
        kh.setSoDienThoai(request.getSoDienThoai());
        kh.setDiaChi(request.getDiaChi());
        kh.setReferralCode(khachHangService.generateNextReferralCode());
        khachHangRepository.save(kh);
        return kh;
    }
    private void createAccount(RegisterRequest request, KhachHang kh){
        Account account = new Account();
        account.setUserName(request.getUsername());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setStatus("ACTIVE");
        account.setIsDeleted(0);
        account.setKhachHang(kh);
        RoleGroup roleGroup = new RoleGroup();
        roleGroup.setRoleGroupId(3L); // USER mặc định
        account.setRoleGroup(roleGroup);

        accountRepository.save(account);
    }
}
