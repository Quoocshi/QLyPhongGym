package hahaha.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import hahaha.DTO.NhanVienRegisterDTO;
import hahaha.enums.LoaiNhanVien;
import hahaha.model.ChuyenMon;
import hahaha.model.RoleGroup;
import hahaha.repository.ChuyenMonRepository;
import hahaha.repository.RoleGroupRepository;
import hahaha.repository.ChuyenMonRepository;
import hahaha.repository.BoMonRepository;
import hahaha.model.BoMon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hahaha.model.Account;
import hahaha.model.NhanVien;
import hahaha.repository.AccountRepository;
import hahaha.repository.NhanVienRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NhanVienServiceImpl implements NhanVienService {
    @Autowired
    NhanVienRepository nhanVienRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private RoleGroupRepository roleGroupRepository;

    @Autowired
    private ChuyenMonRepository chuyenMonRepository;

    @Autowired
    private BoMonRepository boMonRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String generateNextMaNV() {
        List<NhanVien> allNV = nhanVienRepository.findAll();
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
        List<NhanVien> allNV = nhanVienRepository.findAll();
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
        List<NhanVien> allNV = nhanVienRepository.findAll();
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
    public List<NhanVien> searchNhanVien(String keyword) {
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

    @Override
    public List<NhanVien> getTrainersByBoMon(String maBM) {
        return nhanVienRepository.findTrainersByBoMon(maBM);
    }

    @Override
    @Transactional
    public NhanVien createFromDTO(NhanVienRegisterDTO dto) {
        // Kiểm tra email/username đã tồn tại
        if (accountRepository.findAccountByUserName(dto.getUsername()) != null ||
                nhanVienRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email hoặc username đã tồn tại");
        }

        // 1️⃣ Tạo NhanVien
        // 1️⃣ Tạo NhanVien
        NhanVien nhanVien = new NhanVien();
        if (dto.getLoaiNV() == LoaiNhanVien.Trainer) {
            nhanVien.setMaNV(generateNextMaPT());
        } else if (dto.getLoaiNV() == LoaiNhanVien.QuanLy) {
            nhanVien.setMaNV(generateNextMaQL());
        } else {
            nhanVien.setMaNV(generateNextMaNV());
        }

        nhanVien.setTenNV(dto.getTenNV());
        nhanVien.setNgaySinh(dto.getNgaySinh());
        nhanVien.setGioiTinh(dto.getGioiTinh());
        nhanVien.setEmail(dto.getEmail());
        nhanVien.setNgayVaoLam(dto.getNgayVaoLam());
        nhanVien.setLoaiNV(dto.getLoaiNV());
        nhanVienRepository.save(nhanVien);

        // 2️⃣ Tạo Account
        Account account = new Account();
        account.setUserName(dto.getUsername());
        account.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setStatus("ACTIVE");
        account.setIsDeleted(0);
        account.setNhanVien(nhanVien);

        // 3️⃣ Gán role theo loại nhân viên
        Long roleGroupId = getRoleGroupIdByLoaiNV(dto.getLoaiNV());
        RoleGroup roleGroup = roleGroupRepository.findById(roleGroupId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role group"));
        account.setRoleGroup(roleGroup);

        accountRepository.save(account);

        if (dto.getLoaiNV() == LoaiNhanVien.Trainer && dto.getMaBM() != null && !dto.getMaBM().isEmpty()) {
            ChuyenMon chuyenMon = new ChuyenMon();
            chuyenMon.setMaNV(nhanVien.getMaNV());
            chuyenMon.setMaBM(dto.getMaBM());

            BoMon boMon = boMonRepository.findById(dto.getMaBM()).orElse(null);
            if (boMon != null) {
                chuyenMon.setGhiChu("HLV " + boMon.getTenBM());
            } else {
                chuyenMon.setGhiChu("HLV");
            }
            chuyenMonRepository.save(chuyenMon);
        }

        return nhanVien;
    }

    private Long getRoleGroupIdByLoaiNV(LoaiNhanVien loaiNV) {
        return switch (loaiNV) {
            case QuanLy -> 1L;
            case LeTan -> 2L;
            case Trainer -> 4L;
            // case PhongTap -> 2L;
            default -> 2L;
        };
    }

    @Override
    public List<ChuyenMon> findChuyenMonByMaNV(String maNV) {
        return chuyenMonRepository.findByMaNV(maNV);
    }

}