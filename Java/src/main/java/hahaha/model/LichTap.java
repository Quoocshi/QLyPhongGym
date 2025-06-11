package hahaha.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "LICHTAP")
public class LichTap {
    
    @Id
    @Column(name = "MaLT", length = 10)
    private String maLT;
    
    @Column(name = "LoaiLich", length = 10)
    private String loaiLich; // 'Lop' hoặc 'PT'
    
    @Column(name = "Thu", length = 20)
    private String thu; // Thứ trong tuần (VD: "246" = Thứ 2, 4, 6)
    
    @Column(name = "TrangThai", length = 20)
    private String trangThai; // 'Sap dien ra', 'Hoan thanh', 'Huy', 'Hoan'
    
    @ManyToOne
    @JoinColumn(name = "Ca", referencedColumnName = "MaCa")
    private CaTap caTap;
    
    @ManyToOne
    @JoinColumn(name = "MaKH", referencedColumnName = "MaKH")
    private KhachHang khachHang;
    
    @ManyToOne
    @JoinColumn(name = "MaNV", referencedColumnName = "MaNV")
    private NhanVien nhanVien;
    
    @ManyToOne
    @JoinColumn(name = "MaLop", referencedColumnName = "MaLop")
    private Lop lop;

    // Constructors
    public LichTap() {}

    // Getters and Setters
    public String getMaLT() {
        return maLT;
    }

    public void setMaLT(String maLT) {
        this.maLT = maLT;
    }

    public String getLoaiLich() {
        return loaiLich;
    }

    public void setLoaiLich(String loaiLich) {
        this.loaiLich = loaiLich;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }



    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public CaTap getCaTap() {
        return caTap;
    }

    public void setCaTap(CaTap caTap) {
        this.caTap = caTap;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public Lop getLop() {
        return lop;
    }

    public void setLop(Lop lop) {
        this.lop = lop;
    }
} 