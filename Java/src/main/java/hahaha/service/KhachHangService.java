package hahaha.service;

import java.util.List;

import hahaha.model.KhachHang;


public interface  KhachHangService {
    List<KhachHang> getAll();
    KhachHang findById(String maKH);
    // Boolean createCustomer(KhachHang customer);
    Boolean updateCustomer(KhachHang customer);
    Boolean deleteCustomer(String MaKH);
    String generateNextMaKH();
    String generateNextReferralCode();
}
