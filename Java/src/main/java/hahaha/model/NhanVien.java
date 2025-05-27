package hahaha.model;

import java.time.LocalDate;

import hahaha.enums.LoaiNhanVien;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NHANVIEN")
public class NhanVien {
    @Id
    @Column(name = "MaNV", length = 10)
    private String maNV;

    @Column(name = "TenNV", nullable = false, length = 50)
    private String tenNV;

    @Column(name = "NGAYSINH")
    private LocalDate ngaySinh;

    @Column(name = "GIOITINH")
    private String gioiTinh;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "NGAYVAOLAM")
    private LocalDate ngayVaoLam;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOAINV", length = 10)
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

