package hahaha.DTO;
import hahaha.enums.TrangThaiHoaDon;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class HoaDonDTO {
    private String maHD;
    private double tongTien;
    private LocalDateTime ngayLap;
    private TrangThaiHoaDon trangThai;
    private LocalDateTime ngayTT;
}

