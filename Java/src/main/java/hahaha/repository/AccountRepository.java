package hahaha.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hahaha.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.username = ?1 AND a.isDeleted = 0")
    Account findAccountByUsername(String username);

    @Query("SELECT a FROM Account a WHERE a.userId = ?1 AND a.isDeleted = 0")
    Account findByUserId(Long userId);
}
