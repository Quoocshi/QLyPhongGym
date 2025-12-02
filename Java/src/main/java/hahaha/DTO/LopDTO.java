package hahaha.DTO;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Data
public class LopDTO {
    private String maLop;
    private String tenLop;
    private String moTa;
    private int slToiDa;
    private String tinhTrangLop;
    private LocalDate ngayBD;
    private LocalDate ngayKT;
    private String ghiChu;
    private Object boMon;
}
