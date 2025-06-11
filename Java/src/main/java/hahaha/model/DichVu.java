package hahaha.model;

import hahaha.enums.LoaiDichVu;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "DICHVU")
public class DichVu {
    @Id
    @Column(name = "MADV")
    private String maDV;

    @Column(name = "TENDV", nullable = false)
    private String tenDV;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOAIDV", length = 10 )
    private LoaiDichVu loaiDV;

    @Column(name = "THOIHAN")
    private Integer thoiHan;

    @Column(name = "DONGIA")
    private Double donGia;

    @Version
    @Column(name = "version")
    private Integer version;

    @ManyToOne()
    @JoinColumn(name = "MABM", referencedColumnName = "MABM")
    private BoMon boMon;

    public String getMaDV() {
        return maDV;
    }

    public void setMaDV(String maDV) {
        this.maDV = maDV;
    }

    public String getTenDV() {
        return tenDV;
    }

    public void setTenDV(String tenDV) {
        this.tenDV = tenDV;
    }

    public Integer getThoiHan() {
        return thoiHan;
    }

    public void setThoiHan(Integer thoiHan) {
        this.thoiHan = thoiHan;
    }

    public Double getDonGia() {
        return donGia;
    }

    public void setDonGia(Double donGia) {
        this.donGia = donGia;
    }

    public BoMon getBoMon() {
        return boMon;
    }

    public void setBoMon(BoMon boMon) {
        this.boMon = boMon;
    }

    public LoaiDichVu getLoaiDV() {
        return loaiDV;
    }

    public void setLoaiDV(LoaiDichVu loaiDV) {
        this.loaiDV = loaiDV;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
    
    
}
