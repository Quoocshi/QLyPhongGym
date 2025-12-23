package hahaha.service;

import java.util.List;

import hahaha.DTO.NhanVienRegisterDTO;
import hahaha.model.NhanVien;

public interface NhanVienService {
    String generateNextMaNV();

    String generateNextMaQL();

    String generateNextMaPT();

    List<NhanVien> getAll();

    NhanVien findById(String maNV);

    Boolean createNhanVien(NhanVien nhanVien);

    Boolean updateNhanVien(NhanVien nhanVien);

    Boolean deleteNhanVien(String maNV);

    List<NhanVien> searchNhanVien(String keyword);

    List<NhanVien> getTrainersByBoMon(String maBM);

    NhanVien createFromDTO(NhanVienRegisterDTO dto);

}
