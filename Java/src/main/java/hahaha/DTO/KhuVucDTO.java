package hahaha.DTO;

import hahaha.model.KhuVuc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuVucDTO {
    private String maKV;
    private String tenKhuVuc;
    private Integer sucChuaToiDa;
    private String trangThai;
    private String tinhTrang;
    private String maBoMon;   // chỉ lấy mã bộ môn
    private String tenBoMon;  // và tên bộ môn nếu cần

    public KhuVucDTO(KhuVuc kv) {
        this.maKV = kv.getMaKV();
        this.tenKhuVuc = kv.getTenKhuVuc();
        this.sucChuaToiDa = kv.getSucChuaToiDa();
        this.trangThai = kv.getTrangThai();
        this.tinhTrang = kv.getTinhTrang();

        if (kv.getBoMon() != null) {
            this.maBoMon = kv.getBoMon().getMaBM();
            this.tenBoMon = kv.getBoMon().getTenBM();
        }
    }
}
