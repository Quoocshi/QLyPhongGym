package hahaha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import hahaha.model.AccountAssignRoleGroup;
import hahaha.model.AccountAssignRoleGroupId;

public interface AccountAssignRoleGroupRepository extends CrudRepository<AccountAssignRoleGroup, AccountAssignRoleGroupId> {

    @Query("SELECT a.id.roleGroupId FROM AccountAssignRoleGroup a WHERE a.id.accountId = ?1 AND a.isDeleted = 0")
    Long findRoleGroupIdByAccountId(Long accountId);
    
    @Query("SELECT a FROM AccountAssignRoleGroup a WHERE a.id.accountId = ?1 AND a.isDeleted = 0")
    List<AccountAssignRoleGroup> findAllByAccountId(Long accountId);
}
