package hahaha.DTO;

import hahaha.model.NhanVien;
import lombok.Data;

@Data
public class NhanVienDTO {
    private String maNV;
    private String hoTen;
    
    public NhanVienDTO() {}
    
    public NhanVienDTO(String maNV, String hoTen) {
        this.maNV = maNV;
        this.hoTen = hoTen;
    }
    
    public NhanVienDTO(NhanVien nv) {
        this.maNV = nv.getMaNV();
        this.hoTen = nv.getTenNV();
    }
}
