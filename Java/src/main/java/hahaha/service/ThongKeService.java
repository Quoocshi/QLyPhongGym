package hahaha.service;

import java.util.List;

import hahaha.model.DoanhThu;

public interface ThongKeService {
    List<DoanhThu>layDoanhThuTheoNgay();
    Long layTongLuotDangKy();
    Long layTongLuotThanhToan();
    Double layTongDoanhThuThang();
}
