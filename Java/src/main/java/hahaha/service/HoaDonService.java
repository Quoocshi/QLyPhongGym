package hahaha.service;
import java.util.List;

import hahaha.model.HoaDon;
import hahaha.model.KhachHang;

public interface HoaDonService {
    //HoaDon createHoaDon(KhachHang khachHang, List<String> dsMaDV);
    String createHoaDon(KhachHang khachHang, String dsMaDV);
    HoaDon timMaHD(String maHD);
    void thanhToan(String maHD);
    String generateNextMaHD();
}
