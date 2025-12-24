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

        // Get maBM from first ChuyenMon if exists
        if (nv.getDsChuyenMon() != null && !nv.getDsChuyenMon().isEmpty()) {
            this.maBM = nv.getDsChuyenMon().get(0).getMaBM();
        } else {
            this.maBM = null;
        }
    }
}
