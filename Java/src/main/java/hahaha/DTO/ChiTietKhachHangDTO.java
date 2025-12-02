package hahaha.DTO;

import hahaha.model.KhachHang;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor          // tạo constructor không tham số
@AllArgsConstructor         // tạo constructor với tất cả các field
public class ChiTietKhachHangDTO {
    private String maKH;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String referalCode;
    private LocalDate ngaySinh;
    private String gioiTinh;
    public ChiTietKhachHangDTO(KhachHang kh) {
        this.maKH = kh.getMaKH();
        this.hoTen = kh.getHoTen();
        this.soDienThoai = kh.getSoDienThoai();
        this.email = kh.getEmail();
        this.diaChi = kh.getDiaChi();
        this.referalCode = kh.getReferralCode();
        this.ngaySinh = kh.getNgaySinh();
        this.gioiTinh = kh.getGioiTinh();
    }
}