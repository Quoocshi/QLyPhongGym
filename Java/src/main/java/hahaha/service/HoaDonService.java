package hahaha.service;
import java.util.List;

import hahaha.model.HoaDon;
import hahaha.model.KhachHang;

public interface HoaDonService {
    HoaDon createHoaDon(KhachHang khachHang, String dsMaDVString);
    HoaDon timMaHD(String maHD);
    void thanhToan(String maHD);
    String generateNextMaHD();
    
    // Methods missing in controller
    HoaDon taoHoaDon(String maKH, Double tongTien);
    void themChiTietHoaDon(String maHD, String maDV);
    void themChiTietHoaDonVoiLop(String maHD, String maDV, String maLop);
    void themChiTietHoaDonVoiTrainer(String maHD, String maDV, String maNV);
}
