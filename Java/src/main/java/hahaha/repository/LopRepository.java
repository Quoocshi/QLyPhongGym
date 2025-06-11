package hahaha.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import hahaha.enums.TinhTrangLop;
import hahaha.model.Lop;

public interface LopRepository extends JpaRepository<Lop, String> {
    Lop findFirstByBoMon_MaBMAndTinhTrangLop(String maBM, TinhTrangLop tinhTrangLop);
    List<Lop> findByNhanVien_MaNV(String maNV);
    List<Lop> findByBoMon_MaBMAndTinhTrangLop(String maBM, TinhTrangLop tinhTrangLop);
}