package hahaha.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class KhachHangDTO {
    private String maKH;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String referalCode;
    private LocalDate ngaySinh;
    private String gioiTinh;

}