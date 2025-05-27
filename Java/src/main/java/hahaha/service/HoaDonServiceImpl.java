package hahaha.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hahaha.enums.TrangThaiHoaDon;
import hahaha.model.ChiTietDangKyDichVu;
import hahaha.model.DichVu;
import hahaha.model.HoaDon;
import hahaha.model.KhachHang;
import hahaha.repository.ChiTietDangKyDichVuRepository;
import hahaha.repository.DichVuRepository;
import hahaha.repository.HoaDonRepository;

@Service
public class HoaDonServiceImpl implements HoaDonService {

    @Autowired private HoaDonRepository hoaDonRepository;
    @Autowired private DichVuRepository dichVuRepository;
    @Autowired private ChiTietDangKyDichVuRepository chiTietRepository;
    @Autowired private ChiTietDangKyDichVuService chiTietService;

    @Override
public HoaDon createHoaDon(KhachHang khachHang, List<String> dsMaDV) {
    HoaDon hoaDon = new HoaDon();
    hoaDon.setMaHD(generateNextMaHD());
    hoaDon.setKhachHang(khachHang);
    hoaDon.setTrangThai(TrangThaiHoaDon.ChuaThanhToan);

    List<ChiTietDangKyDichVu> dsChiTiet = new ArrayList<>();
    double tongGia = 0;

    Integer base = chiTietRepository.findMaxChiTietDangKyDichVuNumber();
    base = (base != null) ? base + 1 : 1;
    for (String maDV : dsMaDV) {
        DichVu dv = dichVuRepository.findById(maDV).orElseThrow();
        ChiTietDangKyDichVu ct = chiTietService.taoChiTiet(dv, hoaDon, base++);
        dsChiTiet.add(ct);
        tongGia += dv.getDonGia();
    }

    hoaDon.setDsChiTiet(dsChiTiet);
    hoaDon.setTongTien(tongGia);

    hoaDonRepository.save(hoaDon); 
    return hoaDon;
}



    @Override
    public HoaDon timMaHD(String maHD) {
        return hoaDonRepository.findById(maHD).orElseThrow();
    }

    @Override
    public void thanhToan(String maHD) {
        HoaDon hd = hoaDonRepository.findById(maHD).orElseThrow();
        hd.setTrangThai(TrangThaiHoaDon.DaThanhToan);
        hd.setNgayTT(java.time.LocalDateTime.now());
        hoaDonRepository.save(hd);
    }

    @Override
    public String generateNextMaHD(){
        Integer max = hoaDonRepository.findMaxMaHoaDonNumber();
        int next = (max != null) ? max + 1 : 1;
        return String.format("HD%03d", next);
    }

}
