package hahaha.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name ="PACKAGE")
public class GymPackage {
    @Id
    @Column(name = "MAGOI", nullable = false)
    private String maGoi;
    @Column(name = "TENGOI", nullable = false)
    private String tenGoi;
    @Column(name = "LOAIGOI", nullable = false)
    private String loaiGoi;
    @Column(name = "GIATIEN", nullable = false)
    private double giaTien;
    @Column(name = "THOIHAN", nullable = false)
    private int thoiHan;
    public String getMaGoi() {
        return maGoi;
    }
    public void setMaGoi(String maGoi) {
        this.maGoi = maGoi;
    }
    public String getTenGoi() {
        return tenGoi;
    }
    public void setTenGoi(String tenGoi) {
        this.tenGoi = tenGoi;
    }
    public String getLoaiGoi() {
        return loaiGoi;
    }
    public void setLoaiGoi(String loaiGoi) {
        this.loaiGoi = loaiGoi;
    }
    public double getGiaTien() {
        return giaTien;
    }
    public void setGiaTien(double giaTien) {
        this.giaTien = giaTien;
    }
    public int getThoiHan() {
        return thoiHan;
    }
    public void setThoiHan(int thoiHan) {
        this.thoiHan = thoiHan;
    }
}
