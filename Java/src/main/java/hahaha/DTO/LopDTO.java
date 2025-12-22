package hahaha.DTO;

import lombok.Data;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

@Data
public class LopDTO {
    private String maLop;
    private String tenLop;
    private String moTa;
    private int slToiDa;
    private String tinhTrangLop;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate ngayBD;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate ngayKT;
    private String ghiChu;
    private Object boMon;

    // New fields for frontend display
    private String tenGV;
    private String lichHoc;
    private String phong;

    // Fields for creation
    private Integer thoiHan;
    private String maBM;
    private String maNV; // For assigning trainer if needed
}
