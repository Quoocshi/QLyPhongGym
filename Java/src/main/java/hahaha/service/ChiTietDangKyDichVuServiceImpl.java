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
import hahaha.repository.LopRepository;

@Service
public class ChiTietDangKyDichVuServiceImpl implements ChiTietDangKyDichVuService {

    @Autowired
    private LopRepository lopRepository;

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
        ct.setNgayKT(LocalDateTime.now().plusDays(dv.getThoiHan()));

        if (!dv.getLoaiDV().equals(LoaiDichVu.TuDo)) {
            String maBM = dv.getBoMon().getMaBM();
            Lop lop = lopRepository.findFirstByBoMon_MaBMAndTinhTrangLop(maBM, TinhTrangLop.ChuaDay);
            if (lop != null) {
                ct.setLop(lop);
                ct.setNhanVien(lop.getNhanVien());
            }
        }
        return ct;
    }


}
