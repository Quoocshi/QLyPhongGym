package hahaha.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChiTietDichVuDTO {
    private String maDV;
    private String tenDichVu;
    private LocalDateTime ngayBD;
    private LocalDateTime ngayKT;
    private Double giaTien;


}

