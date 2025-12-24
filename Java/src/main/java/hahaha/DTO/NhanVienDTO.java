package hahaha.DTO;

import hahaha.model.NhanVien;
import lombok.Data;

@Data
public class NhanVienDTO {
    private String maNV;
    private String hoTen;
    private String tenNV; // Frontend expects this field name
    private String email;
    private String loaiNV; // Trainer, LeTan, QuanLy
    private String maBM; // Mã bộ môn (for Trainers)

    public NhanVienDTO() {
    }

    public NhanVienDTO(String maNV, String hoTen) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.tenNV = hoTen; // Mirror hoTen to tenNV
    }

    public NhanVienDTO(NhanVien nv) {
        this.maNV = nv.getMaNV();
        this.hoTen = nv.getTenNV();
        this.tenNV = nv.getTenNV(); // keep for backward compatibility
        this.email = nv.getEmail();
        this.loaiNV = nv.getLoaiNV() != null ? nv.getLoaiNV().name() : null;

        // Note: maBM would need to be fetched from CT_CHUYENMON table separately
        // For now, leaving it as null since NhanVien doesn't have this relationship
        // mapped
        this.maBM = null;
    }
}
