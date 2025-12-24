package hahaha.DTO;

import hahaha.enums.TrangThaiHoaDon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonDTO {
    private String maHD;
    private double tongTien;
    private LocalDateTime ngayLap;
    private TrangThaiHoaDon trangThai;
    private LocalDateTime ngayTT;
    private List<ChiTietDichVuDTO> dsChiTiet;
}
