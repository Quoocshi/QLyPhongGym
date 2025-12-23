package hahaha.model;

import java.time.LocalDateTime;

import hahaha.enums.TinhTrangLop;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "LOP")
public class Lop {
    @Id
    @Column(name = "MALOP")
    private String maLop;

    @Column(name = "TENLOP", nullable = false)
    private String tenLop;

    @Column(name = "MOTA")
    private String moTa;

    @Column(name = "SL_TOIDA")
    private int slToiDa;

    @Enumerated(EnumType.STRING)
    @Column(name = "TINHTRANG", length = 10)
    private TinhTrangLop tinhTrangLop;

    @Column(name = "NGAYBD")
    private LocalDateTime ngayBD;

    @Column(name = "NGAYKT")
    private LocalDateTime ngayKT;

    @Column(name = "GHICHU")
    private String ghiChu;

    @OneToOne(optional = true)
    @JoinColumn(name = "MaBM", referencedColumnName = "MaBM")
    private BoMon boMon;

    @OneToOne(optional = true)
    @JoinColumn(name = "MaNV", referencedColumnName = "MaNV")
    private NhanVien nhanVien;

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getSlToiDa() {
        return slToiDa;
    }

    public void setSlToiDa(int slToiDa) {
        this.slToiDa = slToiDa;
    }

    public TinhTrangLop getTinhTrangLop() {
        return tinhTrangLop;
    }

    public void setTinhTrangLop(TinhTrangLop tinhTrangLop) {
        this.tinhTrangLop = tinhTrangLop;
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

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public BoMon getBoMon() {
        return boMon;
    }

    public void setBoMon(BoMon boMon) {
        this.boMon = boMon;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }
}
