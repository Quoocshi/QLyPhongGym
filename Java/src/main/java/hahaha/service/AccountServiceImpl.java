package hahaha.service;

import hahaha.model.Account;
import hahaha.model.KhachHang;
import hahaha.model.RoleGroup;
import hahaha.repository.AccountRepository;
import hahaha.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    KhachHangRepository khachHangRepository;
    @Autowired
    KhachHangService khachHangService;
    @Override
    public Long getAccountIdByUsername(String username) {
        Long accountId = null;
        accountId = accountRepository.findAccountByUserName(username).getAccountId();
        return accountId;
    }
    @Override
    public Account findByEmail(String email) {
        return accountRepository.findAccountByEmail(email);
    }

    @Override
    @Transactional
    public Account registerGoogleUser(String email, String name) {
        // Tạo Khách hàng
        KhachHang kh = new KhachHang();
        kh.setMaKH(khachHangService.generateNextMaKH());
        kh.setHoTen(name);
        kh.setEmail(email);
        kh.setGioiTinh("Khác");
        kh.setNgaySinh(LocalDate.now().minusYears(18)); // default
        kh.setSoDienThoai("N/A");
        kh.setDiaChi("N/A");
        kh.setReferralCode(khachHangService.generateNextReferralCode());
        khachHangRepository.save(kh);

        // Tạo Account
        Account acc = new Account();
        acc.setUserName(email); // username = email Google
        acc.setPasswordHash("GOOGLE"); // dummy password
        acc.setCreatedAt(LocalDateTime.now());
        acc.setUpdatedAt(LocalDateTime.now());
        acc.setStatus("ACTIVE");
        acc.setIsDeleted(0);
        acc.setKhachHang(kh);

        RoleGroup roleGroup = new RoleGroup();
        roleGroup.setRoleGroupId(3L); // USER
        acc.setRoleGroup(roleGroup);

        return accountRepository.save(acc);
    }
}
