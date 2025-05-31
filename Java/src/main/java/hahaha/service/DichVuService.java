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
} 