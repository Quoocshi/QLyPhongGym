package hahaha.model;
import java.time.LocalDateTime;
import java.util.List;

import hahaha.enums.TrangThaiHoaDon;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "HOADON")
public class HoaDon {

    @Id
    @Column(name = "MAHD", length = 10)
    private String maHD;

    @Column(name = "TONGTIEN")
    private Double tongTien;

    @Column(name = "NGAYLAP")
    private LocalDateTime ngayLap;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 50)
    private TrangThaiHoaDon trangThai;

    @Column(name = "NGAYTT")
    private LocalDateTime ngayTT;

    @ManyToOne
    @JoinColumn(name = "MaKH", referencedColumnName = "MaKH")
    private KhachHang khachHang;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL)
    private List<ChiTietDangKyDichVu> dsChiTiet;


    public HoaDon() {}


    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public Double getTongTien() {
        return tongTien;
    }

    public void setTongTien(Double tongTien) {
        this.tongTien = tongTien;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public LocalDateTime getNgayTT() {
        return ngayTT;
    }

    public void setNgayTT(LocalDateTime ngayTT) {
        this.ngayTT = ngayTT;
    }

    public TrangThaiHoaDon getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiHoaDon trangThai) {
        this.trangThai = trangThai;
    }

    public List<ChiTietDangKyDichVu> getDsChiTiet() {
        return dsChiTiet;
    }

    public void setDsChiTiet(List<ChiTietDangKyDichVu> dsChiTiet) {
        this.dsChiTiet = dsChiTiet;
    }
}
