package hahaha.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "CT_DKDV")
public class ChiTietDangKyDichVu {

    @Id
    @Column(name = "MaCTDK")
    private String maCTDK;

    @Column(name = "NGAYBD")
    private LocalDateTime ngayBD;

    @Column(name = "NGAYKT")
    private LocalDateTime ngayKT;

    @Column(name = "DA_HUY")
    private Integer daHuy = 0;

    @OneToOne
    @JoinColumn(name = "MaHD")
    private HoaDon hoaDon;

    @OneToOne
    @JoinColumn(name = "MaDV")
    private DichVu dichVu;

    @OneToOne
    @JoinColumn(name = "MaLop")
    private Lop lop;

    @OneToOne
    @JoinColumn(name = "MaNV")
    private NhanVien nhanVien;

    public String getMaCTDK() {
        return maCTDK;
    }

    public void setMaCTDK(String maCTDK) {
        this.maCTDK = maCTDK;
    }

    public LocalDateTime getNgayBD() {
        return ngayBD;
    }

    public void setNgayBD(LocalDateTime ngayBD) {
        this.ngayBD = ngayBD;
    }

    public LocalDateTime getNgayKT() {
        return ngayKT;
    }

    public void setNgayKT(LocalDateTime ngayKT) {
        this.ngayKT = ngayKT;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
    }

    public Lop getLop() {
        return lop;
    }

    public void setLop(Lop lop) {
        this.lop = lop;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public Integer getDaHuy() {
        return daHuy;
    }

    public void setDaHuy(Integer daHuy) {
        this.daHuy = daHuy;
    }

}
