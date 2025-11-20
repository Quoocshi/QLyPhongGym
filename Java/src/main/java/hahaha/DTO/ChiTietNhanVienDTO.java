package hahaha.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class NhanVienDTO {
    private String maNV;
    private String tenNV;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String email;
    private LocalDate ngayVaoLam;
    private String loaiNV;
}
