package hahaha.DTO;

import hahaha.model.LichTap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LichTapDTO {
    private String maLT;
    private String loaiLich;
    private String thu;
    private String trangThai;
    private String tenCaTap;
    private String moTaCaTap;
    private String tenNhanVien;
    private String tenLop;
    private String tenKhuVuc;

    // Date range fields for filtering (from Lop entity)
    private String ngayBD; // Start date
    private String ngayKT; // End date

    public LichTapDTO(LichTap lichTap) {
        this.maLT = lichTap.getMaLT();
        this.loaiLich = lichTap.getLoaiLich();
        this.thu = lichTap.getThu();
        this.trangThai = lichTap.getTrangThai();

        if (lichTap.getCaTap() != null) {
            this.tenCaTap = lichTap.getCaTap().getTenCa();
            this.moTaCaTap = lichTap.getCaTap().getMoTa();
        }

        if (lichTap.getNhanVien() != null) {
            this.tenNhanVien = lichTap.getNhanVien().getTenNV();
        }

        if (lichTap.getLop() != null) {
            this.tenLop = lichTap.getLop().getTenLop();

            // Populate date range from Lop entity
            if (lichTap.getLop().getNgayBD() != null) {
                this.ngayBD = lichTap.getLop().getNgayBD().toString();
            }
            if (lichTap.getLop().getNgayKT() != null) {
                this.ngayKT = lichTap.getLop().getNgayKT().toString();
            }
        }

        if (lichTap.getKhuVuc() != null) {
            this.tenKhuVuc = lichTap.getKhuVuc().getTenKhuVuc();
        }
    }
}