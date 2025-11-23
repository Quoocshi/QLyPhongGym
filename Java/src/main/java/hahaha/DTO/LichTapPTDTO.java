package hahaha.DTO;

import hahaha.model.LichTap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichTapPTDTO {
    private String maLT;
    private String loaiLich;
    private String thu;
    private String trangThai;

    // CaTap
    private String maCa;
    private String tenCa;
    private String moTaCa;

    // KhachHang
    private String maKH;

    // NhanVien
    private String maNV;
    private String tenNV;

    // Lop
    private String maLop;
    private String tenLop;

    // KhuVuc
    private String maKV;
    private String tenKhuVuc;

    public LichTapPTDTO(LichTap lt) {
        this.maLT = lt.getMaLT();
        this.loaiLich = lt.getLoaiLich();
        this.thu = lt.getThu();
        this.trangThai = lt.getTrangThai();

        // CaTap
        if (lt.getCaTap() != null) {
            this.maCa = lt.getCaTap().getMaCa();
            this.tenCa = lt.getCaTap().getTenCa();
            this.moTaCa = lt.getCaTap().getMoTa();
        }

        // KhachHang
        if (lt.getKhachHang() != null) {
            this.maKH = lt.getKhachHang().getMaKH();
        }

        // NhanVien
        if (lt.getNhanVien() != null) {
            this.maNV = lt.getNhanVien().getMaNV();
            this.tenNV = lt.getNhanVien().getTenNV();
        }

        // Lop
        if (lt.getLop() != null) {
            this.maLop = lt.getLop().getMaLop();
            this.tenLop = lt.getLop().getTenLop();
        }

        // KhuVuc
        if (lt.getKhuVuc() != null) {
            this.maKV = lt.getKhuVuc().getMaKV();
            this.tenKhuVuc = lt.getKhuVuc().getTenKhuVuc();
        }
    }
}

