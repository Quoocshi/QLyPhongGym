package hahaha.DTO;

import hahaha.model.CaTap;
import lombok.Data;

@Data
public class CaTapDTO {
    private String maCa;
    private String tenCa;
    private String moTa;

    public CaTapDTO(CaTap ca) {
        this.maCa = ca.getMaCa();
        this.tenCa = ca.getTenCa();
        this.moTa = ca.getMoTa();
    }
}

