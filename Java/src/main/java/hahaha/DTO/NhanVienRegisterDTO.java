package hahaha.DTO;

import hahaha.enums.LoaiNhanVien;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

@Data
public class NhanVienRegisterDTO {
    private String tenNV;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String email;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate ngayVaoLam;
    private LoaiNhanVien loaiNV;
    private String username;

    private String password;
    private String maBM;
    private String ghiChu;
}
