package hahaha.DTO;

import hahaha.enums.LoaiNhanVien;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NhanVienRegisterDTO {
    private String tenNV;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String email;
    private LocalDate ngayVaoLam;
    private LoaiNhanVien loaiNV;
    private String username;
    private String password;
}
