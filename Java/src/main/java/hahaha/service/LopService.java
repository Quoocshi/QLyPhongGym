package hahaha.service;

import java.util.List;
import hahaha.model.CaTap;
import hahaha.model.Lop;

public interface LopService {
    List<Lop> getLopsByTrainerMaNV(String maNV);
    List<Lop> getAllLop();
    List<Lop> getLopChuaDayByBoMon(String maBM);  // Lấy lớp chưa đầy theo bộ môn
    String getCaTapStringForLop(String maLop);    // Lấy thông tin ca tập của lớp từ LICHTAP
    int getThoiHanLop(Lop lop);                   // Tính thời hạn của lớp từ NgayKT - NgayBD
} 