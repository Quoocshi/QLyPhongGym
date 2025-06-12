package hahaha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hahaha.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.userName = ?1 AND a.isDeleted = 0")
    Account findAccountByUserName(String userName);

    @Query("SELECT a FROM Account a WHERE a.accountId = ?1 AND a.isDeleted = 0")
    Account findByAccountId(Long accountId);

    @Query("SELECT a.roleGroup.roleGroupId FROM Account a WHERE a.accountId = ?1 AND a.isDeleted = 0")
    Long findRoleGroupIdByAccountId(Long accountId);
    
    @Query("""
        SELECT a.accountId FROM Account a
        LEFT JOIN a.khachHang kh
        LEFT JOIN a.nhanVien nv
        WHERE 
            ((kh.email = ?1) OR (nv.email = ?1))
            AND a.isDeleted = 0
    """)
    Long findAccountIdByEmail(String email);

    @Query("""
        SELECT a
        FROM Account a
        LEFT JOIN a.khachHang kh
        LEFT JOIN a.nhanVien nv
        WHERE (
            (kh.email = ?1) OR (nv.email = ?1)
        )
        AND a.isDeleted = 0
    """)
    Account findAccountByEmail(String email);


    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Account a
        LEFT JOIN a.khachHang kh
        LEFT JOIN a.nhanVien nv
        WHERE (
            (kh.email = ?1) OR (nv.email = ?1)
        )
        AND a.isDeleted = 0
    """)
    boolean existsByEmail(String email);

    @Query("SELECT a FROM Account a WHERE a.khachHang.maKH = ?1 AND a.isDeleted = 0")
    Account findByKhachHang_MaKH(String maKH);

    @Query("SELECT a FROM Account a WHERE a.nhanVien.maNV = ?1 AND a.isDeleted = 0")
    Account findByNhanVien_MaNV(String maNV);

}
