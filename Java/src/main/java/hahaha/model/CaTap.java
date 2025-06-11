package hahaha.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CATAP")
public class CaTap {
    
    @Id
    @Column(name = "MaCa", length = 10)
    private String maCa;
    
    @Column(name = "TenCa", nullable = false, length = 50)
    private String tenCa;
    
    @Column(name = "MoTa", length = 100)
    private String moTa;

    // Constructors
    public CaTap() {}
    
    public CaTap(String maCa, String tenCa, String moTa) {
        this.maCa = maCa;
        this.tenCa = tenCa;
        this.moTa = moTa;
    }

    // Getters and Setters
    public String getMaCa() {
        return maCa;
    }

    public void setMaCa(String maCa) {
        this.maCa = maCa;
    }

    public String getTenCa() {
        return tenCa;
    }

    public void setTenCa(String tenCa) {
        this.tenCa = tenCa;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
} 