package hahaha.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hahaha.model.KhachHang;

public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    @Query("SELECT MAX(CAST(SUBSTRING(k.maKH, 3) AS int)) FROM KhachHang k WHERE k.maKH LIKE 'KH%'")
    Integer findMaxMaKHNumber();

    boolean existsByReferralCode(String referralCode);

    @Query("SELECT MAX(CAST(SUBSTRING(k.referralCode, 4) AS int)) FROM KhachHang k WHERE k.referralCode LIKE 'REF%'")
    Integer findMaxReferralCodeNumber();

    KhachHang findByEmail(String email);

}
