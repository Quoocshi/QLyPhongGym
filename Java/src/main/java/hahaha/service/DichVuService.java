package hahaha.service;

import java.util.List;

import hahaha.model.BoMon;
import hahaha.model.DichVu;

public interface DichVuService {
    List<DichVu> getAll();
    DichVu findById(String maDV);
    Boolean createDichVu(DichVu dichVu);
    Boolean updateDichVu(DichVu dichVu);
    Boolean deleteDichVu(String maDV);
    String generateNextMaDV();
    List<BoMon> getAllBoMon();

    List<DichVu> getDichVuTheoBoMonKhachHangChuaDangKy(String maBM, String maKH);
    
    // Method mới để filter theo thời hạn
    List<DichVu> getDichVuTheoBoMonVaThoiHanKhachHangChuaDangKy(String maBM, String maKH, String thoiHanFilter);
    
//    // Methods mới: Hiển thị tất cả dịch vụ theo bộ môn (không filter dịch vụ đã đăng ký)
//    List<DichVu> getTatCaDichVuTheoBoMon(String maBM);
//    List<DichVu> getTatCaDichVuTheoBoMonVaThoiHan(String maBM, String thoiHanFilter);

    // Methods missing in controller
//    List<DichVu> getDichVuByBoMon(String maBM);
    BoMon getBoMonById(String maBM);
} 