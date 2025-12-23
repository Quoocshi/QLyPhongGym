package hahaha.DTO;

import hahaha.model.NhanVien;
import lombok.Data;

@Data
public class NhanVienDTO {
    private String maNV;
    private String tenNV;
    private String hoTen; // Keep for backward compatibility
    private String loaiNV;
    private String maBM;

    public NhanVienDTO() {
    }

    public NhanVienDTO(String maNV, String hoTen) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.tenNV = hoTen;
    }

    public NhanVienDTO(NhanVien nv) {
        this.maNV = nv.getMaNV();
        this.tenNV = nv.getTenNV();
        this.hoTen = nv.getTenNV(); // Keep for backward compatibility
        this.loaiNV = nv.getLoaiNV() != null ? nv.getLoaiNV().toString() : null;

        // Get maBM from first ChuyenMon if exists
        if (nv.getDsChuyenMon() != null && !nv.getDsChuyenMon().isEmpty()) {
            this.maBM = nv.getDsChuyenMon().get(0).getMaBM();
        } else {
            this.maBM = null;
        }
    }
}
