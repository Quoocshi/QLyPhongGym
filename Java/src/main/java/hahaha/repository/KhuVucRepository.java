package hahaha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hahaha.model.KhuVuc;

public interface KhuVucRepository extends JpaRepository<KhuVuc, String> {
    
    // Tìm khu vực theo bộ môn
    List<KhuVuc> findByBoMon_MaBM(String maBM);
    
    // Tìm khu vực hoạt động theo bộ môn
    @Query("SELECT kv FROM KhuVuc kv WHERE kv.boMon.maBM = :maBM AND kv.trangThai = 'HoatDong'")
    List<KhuVuc> findActiveByBoMon(@Param("maBM") String maBM);
    
    // Tìm khu vực có sẵn (hoạt động và chưa đầy)
    @Query("SELECT kv FROM KhuVuc kv WHERE kv.trangThai = 'HoatDong' AND kv.tinhTrang = 'ChuaDay'")
    List<KhuVuc> findAvailableKhuVuc();
} 