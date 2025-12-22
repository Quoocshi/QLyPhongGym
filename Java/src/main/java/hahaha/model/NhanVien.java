package hahaha.model;

import java.time.LocalDate;

import hahaha.enums.LoaiNhanVien;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "NHANVIEN")
public class NhanVien {

    @Id
    @Column(name = "MaNV", length = 10)
    @NotBlank(message = "Mã nhân viên không được để trống")
    @Size(min = 3, max = 10, message = "Mã nhân viên phải từ 3-10 ký tự")
    private String maNV;

    @Column(name = "TenNV", nullable = false, length = 50)
    @NotBlank(message = "Tên nhân viên không được để trống")
    @Size(min = 2, max = 50, message = "Tên nhân viên phải từ 2-50 ký tự")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Tên nhân viên chỉ chứa chữ cái và khoảng trắng")
    private String tenNV;

    @Column(name = "NGAYSINH")
    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate ngaySinh;

    @Column(name = "GIOITINH")
    @NotBlank(message = "Giới tính không được để trống")
    @Pattern(regexp = "^(Nam|Nữ)$", message = "Giới tính phải là Nam hoặc Nữ")
    private String gioiTinh;

    @Column(name = "EMAIL", nullable = false)
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được quá 100 ký tự")
    private String email;

    @Column(name = "NGAYVAOLAM")
    @NotNull(message = "Ngày vào làm không được để trống")
    @PastOrPresent(message = "Ngày vào làm không thể là ngày tương lai")
    private LocalDate ngayVaoLam;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOAINV", length = 10)
    @NotNull(message = "Loại nhân viên không được để trống")
    private LoaiNhanVien loaiNV;

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public LoaiNhanVien getLoaiNV() {
        return loaiNV;
    }

    public void setLoaiNV(LoaiNhanVien loaiNV) {
        this.loaiNV = loaiNV;
    }
}

