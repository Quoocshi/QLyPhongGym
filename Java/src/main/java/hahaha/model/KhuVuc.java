package hahaha.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "KHUVUC")
public class KhuVuc {
    
    @Id
    @Column(name = "MaKV", length = 10)
    private String maKV;
    
    @Column(name = "TenKhuVuc", nullable = false, length = 20)
    private String tenKhuVuc;
    
    @Column(name = "SucChuaToiDa")
    private Integer sucChuaToiDa;
    
    @Column(name = "TrangThai", length = 10)
    private String trangThai; // 'HoatDong' hoặc 'BaoTri'
    
    @Column(name = "TinhTrang", length = 10)
    private String tinhTrang; // 'ChuaDay' hoặc 'DaDay'
    
    @ManyToOne
    @JoinColumn(name = "MaBM", referencedColumnName = "MaBM")
    private BoMon boMon;

    // Constructors
    public KhuVuc() {}
    
    public KhuVuc(String maKV, String tenKhuVuc, Integer sucChuaToiDa, String trangThai, String tinhTrang, BoMon boMon) {
        this.maKV = maKV;
        this.tenKhuVuc = tenKhuVuc;
        this.sucChuaToiDa = sucChuaToiDa;
        this.trangThai = trangThai;
        this.tinhTrang = tinhTrang;
        this.boMon = boMon;
    }

    // Getters and Setters
    public String getMaKV() {
        return maKV;
    }

    public void setMaKV(String maKV) {
        this.maKV = maKV;
    }

    public String getTenKhuVuc() {
        return tenKhuVuc;
    }

    public void setTenKhuVuc(String tenKhuVuc) {
        this.tenKhuVuc = tenKhuVuc;
    }

    public Integer getSucChuaToiDa() {
        return sucChuaToiDa;
    }

    public void setSucChuaToiDa(Integer sucChuaToiDa) {
        this.sucChuaToiDa = sucChuaToiDa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public BoMon getBoMon() {
        return boMon;
    }

    public void setBoMon(BoMon boMon) {
        this.boMon = boMon;
    }
} 