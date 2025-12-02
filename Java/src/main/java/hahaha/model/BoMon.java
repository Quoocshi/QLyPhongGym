package hahaha.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "maBM")
@Entity
@Table(name = "BOMON")
public class BoMon {

    @Id    
    @Column(name = "MABM")
    private String maBM;
    @Column(name = "TENBM", nullable = false)
    private String tenBM;

    @OneToMany(mappedBy = "boMon")
//    @JsonManagedReference
//    @JsonIgnore
    private Set<DichVu> danhSachDichVu;


    public String getMaBM() {
        return maBM;
    }
    public void setMaBM(String maBM) {
        this.maBM = maBM;
    }
    public String getTenBM() {
        return tenBM;
    }
    public void setTenBM(String tenBM) {
        this.tenBM = tenBM;
    }

    public Set<DichVu> getDanhSachDichVu() {
        return danhSachDichVu;
    }

    public void setDanhSachDichVu(Set<DichVu> danhSachDichVu) {
        this.danhSachDichVu = danhSachDichVu;
    }
    
}
