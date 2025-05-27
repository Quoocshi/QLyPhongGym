package hahaha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hahaha.repository.KhachHangRepository;

@Service
public class KhachHangServiceImpl implements KhachHangService{
    @Autowired
        KhachHangRepository khachHangRepository;

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


    // @Override
    // public List<KhachHang> getAll() {
    //     return KhachHangRepository.findAll();
    // }

    // @Override
    // public Boolean createCustomer(KhachHang customer) {
    //     try{
    //         customerRepository.save(customer);
    //         return true;

    //     }catch(Exception e){
    //         e.printStackTrace();
    //     }
    //     return false;

    // }

    // @Override
    // public Boolean updateCustomer(KhachHang customer) {
    //     try{
    //         customerRepository.save(customer);
    //         return true;

    //     }catch(Exception e){
    //         e.printStackTrace();
    //     }
    //     return false;
    // }

    // @Override
    // public Boolean deleteCustomer(String MaKH) {
    //     try{
    //         customerRepository.deleteById(MaKH);
    //         return true;

    //     }catch(Exception e){
    //         e.printStackTrace();
    //     }
    //     return false;
    // }

    // @Override
    // public KhachHang findById(String id) {
    //     return customerRepository.findById(id).get();
    // }

}
