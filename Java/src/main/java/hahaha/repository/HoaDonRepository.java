package hahaha.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hahaha.model.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    @Query("SELECT MAX(CAST(SUBSTRING(h.maHD, 3) AS int)) FROM HoaDon h WHERE h.maHD LIKE 'HD%'")
    Integer findMaxMaHoaDonNumber();

    
}
