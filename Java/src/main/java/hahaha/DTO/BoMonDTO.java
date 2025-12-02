package hahaha.DTO;

import lombok.Data;

import java.util.List;

@Data
public class BoMonDTO {
    private String maBM;
    private String tenBM;
    private List<DichVuDTO> danhSachDichVu;
}
