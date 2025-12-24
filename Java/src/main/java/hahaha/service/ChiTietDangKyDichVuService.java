package hahaha.service;

import hahaha.model.ChiTietDangKyDichVu;
import hahaha.model.DichVu;
import hahaha.model.HoaDon;

public interface ChiTietDangKyDichVuService {
    String generateMaCTDKFromNumber(int number);

    ChiTietDangKyDichVu taoChiTiet(DichVu dv, HoaDon hd, int stt);

    ChiTietDangKyDichVu taoChiTietVoiLop(DichVu dv, HoaDon hd, int stt, String maLop);

    ChiTietDangKyDichVu taoChiTietVoiTrainer(DichVu dv, HoaDon hd, int stt, String maNV);

    void huyDichVu(String maCTDK, String maKH) throws Exception;
}
