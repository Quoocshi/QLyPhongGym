package hahaha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import hahaha.repository.NhanVienRepository;
import hahaha.repository.AccountRepository;
import hahaha.model.NhanVien;
import hahaha.model.Account;
import hahaha.model.KhachHang;
import hahaha.enums.LoaiNhanVien;

@Service
public class NhanVienServiceImpl implements NhanVienService {
    @Autowired
    NhanVienRepository nhanVienRepository;
    
    @Autowired
    AccountRepository accountRepository;

    @Override
    public String generateNextMaNV() {
        List<NhanVien> allNV = nhanVienRepository.findAllNotDeleted();
        int maxNumber = 0;
        for (NhanVien nv : allNV) {
            if (nv.getMaNV().startsWith("NV")) {
                try {
                    int num = Integer.parseInt(nv.getMaNV().substring(2));
                    maxNumber = Math.max(maxNumber, num);
                } catch (NumberFormatException e) {
                    // Ignore invalid format
                }
            }
        }
        return String.format("NV%03d", maxNumber + 1);
    }

    @Override
    public String generateNextMaQL() {
        List<NhanVien> allNV = nhanVienRepository.findAllNotDeleted();
        int maxNumber = 0;
        for (NhanVien nv : allNV) {
            if (nv.getMaNV().startsWith("QL")) {
                try {
                    int num = Integer.parseInt(nv.getMaNV().substring(2));
                    maxNumber = Math.max(maxNumber, num);
                } catch (NumberFormatException e) {
                    // Ignore invalid format
                }
            }
        }
        return String.format("QL%03d", maxNumber + 1);
    }

    @Override
    public String generateNextMaPT() {
        List<NhanVien> allNV = nhanVienRepository.findAllNotDeleted();
        int maxNumber = 0;
        for (NhanVien nv : allNV) {
            if (nv.getMaNV().startsWith("PT")) {
                try {
                    int num = Integer.parseInt(nv.getMaNV().substring(2));
                    maxNumber = Math.max(maxNumber, num);
                } catch (NumberFormatException e) {
                    // Ignore invalid format
                }
            }
        }
        return String.format("PT%03d", maxNumber + 1);
    }

    @Override
    public List<NhanVien> getAll() {
        return nhanVienRepository.findAllActive();
    }

    @Override
    public List<NhanVien> searchNhanVien(String keyword){
        keyword = keyword.trim().replaceAll("\\s+", " ");
        return nhanVienRepository.searchActiveEmployeesByKeyword(keyword);
    }


    @Override
    public NhanVien findById(String maNV) {
        Optional<NhanVien> result = nhanVienRepository.findByIdActive(maNV);
        return result.orElse(null);
    }

    @Override
    public Boolean createNhanVien(NhanVien nhanVien) {
        try {
            nhanVienRepository.save(nhanVien);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean updateNhanVien(NhanVien nhanVien) {
        try {
            if (nhanVienRepository.existsById(nhanVien.getMaNV())) {
                nhanVienRepository.save(nhanVien);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean deleteNhanVien(String maNV) {
        try {
            // Tìm account của nhân viên và soft delete
            Account account = accountRepository.findByNhanVien_MaNV(maNV);
            if (account != null) {
                account.setIsDeleted(1); // Soft delete account
                accountRepository.save(account);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}