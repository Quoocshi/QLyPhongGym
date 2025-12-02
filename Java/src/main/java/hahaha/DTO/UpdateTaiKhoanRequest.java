package hahaha.DTO;

import lombok.Data;

@Data
public class UpdateTaiKhoanRequest {
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private String gioiTinh;
    private String ngaySinh; // yyyy-MM-dd
}