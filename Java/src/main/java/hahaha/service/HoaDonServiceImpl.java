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
        hoaDon.setNgayLap(java.time.LocalDateTime.now());

        List<ChiTietDangKyDichVu> dsChiTiet = new ArrayList<>();
        double tongGia = 0;

        Integer base = chiTietRepository.findMaxChiTietDangKyDichVuNumber();
        base = (base != null) ? base + 1 : 1;
        
        for (String maDV : dsMaDV) {
            try {
                DichVu dv = dichVuRepository.findById(maDV).orElse(null);
                if (dv != null) {
                    ChiTietDangKyDichVu ct = chiTietService.taoChiTiet(dv, hoaDon, base++);
                    dsChiTiet.add(ct);
                    tongGia += dv.getDonGia();
                } else {
                    // Tạo dịch vụ mẫu nếu không tìm thấy trong database
                    DichVu dvMau = new DichVu();
                    dvMau.setMaDV(maDV);
                    dvMau.setTenDV(getMockServiceName(maDV));
                    dvMau.setDonGia(6999999.0);
                    dvMau.setThoiHan(180); // 6 tháng
                    
                    ChiTietDangKyDichVu ct = new ChiTietDangKyDichVu();
                    ct.setMaCTDK(chiTietService.generateMaCTDKFromNumber(base++));
                    ct.setDichVu(dvMau);
                    ct.setHoaDon(hoaDon);
                    ct.setNgayBD(java.time.LocalDateTime.now());
                    ct.setNgayKT(java.time.LocalDateTime.now().plusDays(dvMau.getThoiHan()));
                    
                    dsChiTiet.add(ct);
                    tongGia += dvMau.getDonGia();
                }
            } catch (Exception e) {
                System.err.println("Error processing service: " + maDV + ", " + e.getMessage());
            }
        }

        hoaDon.setDsChiTiet(dsChiTiet);
        hoaDon.setTongTien(tongGia);

        hoaDonRepository.save(hoaDon); 
        return hoaDon;
    }

    private String getMockServiceName(String maDV) {
        switch (maDV.toUpperCase()) {
            case "GYM": return "GYM - 6 tháng - Tự do";
            case "YOGA": return "Lớp YOGA - 6 tháng - Lớp B1";
            case "ZUMBA": return "Lớp ZUMBA - 6 tháng - Lớp A1";
            case "CARDIO": return "CARDIO - 6 tháng - Tự do";
            case "BƠI": return "BƠI - 6 tháng - Tự do";
            case "GYMPT": return "GYM PT - 6 tháng - Cá nhân";
            default: return maDV + " - 6 tháng";
        }
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
