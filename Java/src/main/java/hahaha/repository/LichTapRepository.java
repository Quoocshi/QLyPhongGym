package hahaha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hahaha.model.LichTap;

public interface LichTapRepository extends JpaRepository<LichTap, String> {
    
    // Tìm lịch tập theo mã lớp
    List<LichTap> findByLop_MaLop(String maLop);
    
    // Tìm lịch tập theo mã lớp và loại lịch
    List<LichTap> findByLop_MaLopAndLoaiLich(String maLop, String loaiLich);
    
    // Lấy các ca tập duy nhất cho một lớp
    @Query("SELECT DISTINCT lt.caTap FROM LichTap lt WHERE lt.lop.maLop = :maLop AND lt.loaiLich = 'Lop'")
    List<hahaha.model.CaTap> findDistinctCaTapByMaLop(@Param("maLop") String maLop);
} 