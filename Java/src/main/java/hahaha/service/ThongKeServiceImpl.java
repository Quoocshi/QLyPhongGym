package hahaha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hahaha.model.DoanhThu;
import hahaha.repository.HoaDonRepository;

@Service
public class ThongKeServiceImpl implements ThongKeService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Override
    public List<DoanhThu> layDoanhThuTheoNgay() {
        return hoaDonRepository.thongKeDoanhThuTheoNgay();
    }
}