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
    
    @Query("SELECT a.accountId FROM Account a WHERE a.email = ?1")
    Long findAccountIdByEmail(String email);

    @Query("SELECT a FROM Account a WHERE a.email = ?1 AND a.isDeleted = 0")
    Account findAccountByEmail(String email);

    boolean existsByEmail(String email);

}
