package hahaha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hahaha.model.Account;
import hahaha.model.KhachHang;
import hahaha.repository.AccountRepository;
import hahaha.repository.KhachHangRepository;
import jakarta.transaction.Transactional;

@Service
public class KhachHangServiceImpl implements KhachHangService{
    @Autowired
        KhachHangRepository khachHangRepository;

    @Autowired
        AccountRepository accountRepository;

    @Override
    public String generateNextMaKH() {
        Integer max = khachHangRepository.findMaxMaKHNumber();
        int next = (max != null) ? max + 1 : 1;
        return String.format("KH%03d", next);
    }

    @Override
    public String generateNextReferralCode() {
        Integer max = khachHangRepository.findMaxReferralCodeNumber();
        int next = (max != null) ? max + 1 : 1;
        return String.format("REF%03d", next);
    }


    @Override
    public List<KhachHang> getAll() {
        return khachHangRepository.findAllWithActiveAccount();
    }

    // @Override
    // public Boolean createCustomer(KhachHang khachHang) {
    //     try{
    //         khachHangRepository.save(khachHang);
    //         return true;

    //     }catch(Exception e){
    //         e.printStackTrace();
    //     }
    //     return false;

    // }

    @Override
    public Boolean updateCustomer(KhachHang khachHang) {
        try{
            khachHangRepository.save(khachHang);
            return true;

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean deleteCustomer(String MaKH) {
        try{
            // Tìm account của khách hàng và soft delete
            Account account = accountRepository.findByKhachHang_MaKH(MaKH);
            if (account != null) {
                account.setIsDeleted(1);
                accountRepository.save(account);
                return true;
            }
            return false;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public KhachHang findById(String maKH) {
        return khachHangRepository.findByMaKH(maKH);
    }

}
