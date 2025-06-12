package hahaha.model;

import java.io.Serializable;
import java.util.Objects;

public class ChuyenMonId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String maNV;
    private String maBM;
    
    // Default constructor
    public ChuyenMonId() {}
    
    public ChuyenMonId(String maNV, String maBM) {
        this.maNV = maNV;
        this.maBM = maBM;
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
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChuyenMonId that = (ChuyenMonId) o;
        return Objects.equals(maNV, that.maNV) && 
               Objects.equals(maBM, that.maBM);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(maNV, maBM);
    }
} 