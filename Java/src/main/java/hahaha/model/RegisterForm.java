package hahaha.model;

import java.time.LocalDate;
import jakarta.validation.constraints.*;

public class RegisterForm {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3-50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Tên đăng nhập chỉ chứa chữ, số, dấu chấm, gạch dưới và gạch ngang")
    private String userName;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 3, max = 100, message = "Mật khẩu phải từ 3-100 ký tự")
    private String password;

    // Khách hàng
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2-100 ký tự")
    private String hoTen;
    
    @NotBlank(message = "Giới tính không được để trống")
    @Pattern(regexp = "^(Nam|Nữ|Khác)$", message = "Giới tính phải là Nam, Nữ hoặc Khác")
    private String gioiTinh;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được quá 100 ký tự")
    private String email;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    // Không dùng @Pattern vì ta sẽ validate manual sau khi clean
    private String soDienThoai;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(min = 5, max = 200, message = "Địa chỉ phải từ 5-200 ký tự")
    private String diaChi;
    
    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate ngaySinh;

    // Method để validate tuổi - sẽ được gọi manually
    public boolean isValidAge() {
        if (ngaySinh == null) return false;
        int age = java.time.Period.between(ngaySinh, java.time.LocalDate.now()).getYears();
        return age >= 16 && age <= 100;
    }
    
    public int getAge() {
        if (ngaySinh == null) return 0;
        return java.time.Period.between(ngaySinh, java.time.LocalDate.now()).getYears();
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getHoTen() {
        return hoTen;
    }
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }
    public String getGioiTinh() {
        return gioiTinh;
    }
    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }
    public String getSoDienThoai() {
        return soDienThoai;
    }
    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
    public String getDiaChi() {
        return diaChi;
    }
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
    public LocalDate getNgaySinh() {
        return ngaySinh;
    }
    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
}

