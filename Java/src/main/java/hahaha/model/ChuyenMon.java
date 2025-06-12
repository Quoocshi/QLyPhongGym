package hahaha.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "CT_CHUYENMON")
@IdClass(ChuyenMonId.class)
public class ChuyenMon {
    
    @Id
    @Column(name = "MaNV")
    private String maNV;
    
    @Id
    @Column(name = "MaBM")
    private String maBM;
    
    @Column(name = "GHICHU")
    private String ghiChu;
    
    @ManyToOne
    @JoinColumn(name = "MaNV", referencedColumnName = "MaNV", insertable = false, updatable = false)
    private NhanVien nhanVien;
    
    @ManyToOne
    @JoinColumn(name = "MaBM", referencedColumnName = "MaBM", insertable = false, updatable = false)
    private BoMon boMon;

    // Constructors
    public ChuyenMon() {}
    
    public ChuyenMon(String maNV, String maBM, String ghiChu) {
        this.maNV = maNV;
        this.maBM = maBM;
        this.ghiChu = ghiChu;
    }

    // Getters and Setters
    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getMaBM() {
        return maBM;
    }

    public void setMaBM(String maBM) {
        this.maBM = maBM;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public BoMon getBoMon() {
        return boMon;
    }

    public void setBoMon(BoMon boMon) {
        this.boMon = boMon;
    }
} 