package hahaha.repository;

import hahaha.model.ChuyenMon;
import hahaha.model.ChuyenMonId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChuyenMonRepository extends JpaRepository<ChuyenMon, ChuyenMonId> {
    List<ChuyenMon> findByMaNV(String maNV);
}
