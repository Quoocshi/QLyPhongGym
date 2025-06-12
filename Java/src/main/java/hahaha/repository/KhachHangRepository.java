package hahaha.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hahaha.model.KhachHang;

public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    @Query("SELECT MAX(CAST(SUBSTRING(k.maKH, 3) AS int)) FROM KhachHang k WHERE k.maKH LIKE 'KH%'")
    Integer findMaxMaKHNumber();

    boolean existsByReferralCode(String referralCode);

    @Query("SELECT MAX(CAST(SUBSTRING(k.referralCode, 4) AS int)) FROM KhachHang k WHERE k.referralCode LIKE 'REF%'")
    Integer findMaxReferralCodeNumber();

    KhachHang findByEmail(String email);

    KhachHang findByAccount_AccountId(Long accountId);

     // Chỉ lấy khách hàng có account với isDeleted = 0
    @Query("SELECT k FROM KhachHang k JOIN k.account a WHERE a.isDeleted = 0")
    List<KhachHang> findAllWithActiveAccount();

    KhachHang findByMaKH(String maKH);

    @Query("SELECT kh FROM KhachHang kh WHERE " +
       "LOWER(kh.maKH) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(kh.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(kh.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +              
        "kh.soDienThoai LIKE CONCAT('%', :keyword, '%')")
    List<KhachHang> searchByKeyword(@Param("keyword") String keyword);

    // Thêm method kiểm tra số điện thoại trùng lặp
    KhachHang findBySoDienThoai(String soDienThoai);

    // Kiểm tra sự tồn tại của số điện thoại
    boolean existsBySoDienThoai(String soDienThoai);

}
