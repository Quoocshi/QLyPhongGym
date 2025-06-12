package hahaha.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hahaha.enums.LoaiDichVu;
import hahaha.enums.TinhTrangLop;
import hahaha.model.ChiTietDangKyDichVu;
import hahaha.model.DichVu;
import hahaha.model.HoaDon;
import hahaha.model.Lop;
import hahaha.model.NhanVien;
import hahaha.repository.ChiTietDangKyDichVuRepository;
import hahaha.repository.LopRepository;
import hahaha.repository.NhanVienRepository;

@Service
public class ChiTietDangKyDichVuServiceImpl implements ChiTietDangKyDichVuService {

    @Autowired
    private LopRepository lopRepository;
    
    @Autowired
    private ChiTietDangKyDichVuRepository chiTietRepository;
    
    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Override
    public String generateMaCTDKFromNumber(int number) {
        return String.format("CT%03d", number);
    }

    @Override
    public ChiTietDangKyDichVu taoChiTiet(DichVu dv, HoaDon hd, int soThuTu) {
        ChiTietDangKyDichVu ct = new ChiTietDangKyDichVu();
        ct.setMaCTDK(generateMaCTDKFromNumber(soThuTu));
        ct.setDichVu(dv);
        ct.setHoaDon(hd);
        ct.setNgayBD(LocalDateTime.now());
        ct.setNgayKT(LocalDateTime.now().plusDays(dv.getThoiHan() != null ? dv.getThoiHan() : 30));

        if (!dv.getLoaiDV().equals(LoaiDichVu.TuDo)) {
            String maBM = dv.getBoMon().getMaBM();
            Lop lop = lopRepository.findFirstByBoMon_MaBMAndTinhTrangLop(maBM, TinhTrangLop.ChuaDay);
            if (lop != null) {
                ct.setLop(lop);
                ct.setNhanVien(lop.getNhanVien());
            }
        }
        
        // Lưu vào database
        return chiTietRepository.save(ct);
    }

    @Override
    public ChiTietDangKyDichVu taoChiTietVoiLop(DichVu dv, HoaDon hd, int soThuTu, String maLop) {
        ChiTietDangKyDichVu ct = new ChiTietDangKyDichVu();
        ct.setMaCTDK(generateMaCTDKFromNumber(soThuTu));
        ct.setDichVu(dv);
        ct.setHoaDon(hd);
        ct.setNgayBD(LocalDateTime.now());
        
        // Nếu có lớp được chọn cụ thể
        if (maLop != null && !maLop.trim().isEmpty()) {
            Lop lopDaChon = lopRepository.findById(maLop).orElse(null);
            if (lopDaChon != null) {
                ct.setLop(lopDaChon);
                ct.setNhanVien(lopDaChon.getNhanVien());
                
                // Sử dụng thời hạn của lớp thay vì dịch vụ
                if (lopDaChon.getNgayBD() != null && lopDaChon.getNgayKT() != null) {
                    ct.setNgayBD(lopDaChon.getNgayBD());
                    ct.setNgayKT(lopDaChon.getNgayKT());
                } else {
                    // Fallback về thời hạn dịch vụ nếu lớp không có ngày
                    ct.setNgayKT(LocalDateTime.now().plusDays(dv.getThoiHan() != null ? dv.getThoiHan() : 30));
                }
            } else {
                // Lớp không tồn tại, fallback về logic cũ
                ct.setNgayKT(LocalDateTime.now().plusDays(dv.getThoiHan() != null ? dv.getThoiHan() : 30));
            }
        } else {
            // Không có lớp được chọn, sử dụng logic cũ
            ct.setNgayKT(LocalDateTime.now().plusDays(dv.getThoiHan() != null ? dv.getThoiHan() : 30));
            
            if (!dv.getLoaiDV().equals(LoaiDichVu.TuDo)) {
                String maBM = dv.getBoMon().getMaBM();
                Lop lop = lopRepository.findFirstByBoMon_MaBMAndTinhTrangLop(maBM, TinhTrangLop.ChuaDay);
                if (lop != null) {
                    ct.setLop(lop);
                    ct.setNhanVien(lop.getNhanVien());
                }
            }
        }
        
        // Lưu vào database
        return chiTietRepository.save(ct);
    }

    @Override
    public ChiTietDangKyDichVu taoChiTietVoiTrainer(DichVu dv, HoaDon hd, int soThuTu, String maNV) {
        ChiTietDangKyDichVu ct = new ChiTietDangKyDichVu();
        ct.setMaCTDK(generateMaCTDKFromNumber(soThuTu));
        ct.setDichVu(dv);
        ct.setHoaDon(hd);
        ct.setNgayBD(LocalDateTime.now());
        ct.setNgayKT(LocalDateTime.now().plusDays(dv.getThoiHan() != null ? dv.getThoiHan() : 30));
        
        // Gán trainer được chọn
        if (maNV != null && !maNV.isEmpty()) {
            NhanVien trainer = nhanVienRepository.findById(maNV).orElse(null);
            if (trainer != null) {
                ct.setNhanVien(trainer);
                System.out.println("Đã gán trainer: " + trainer.getTenNV() + " cho dịch vụ PT");
            }
        }
        
        // Lưu vào database
        return chiTietRepository.save(ct);
    }
}
