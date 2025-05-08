package hahaha.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hahaha.model.RoleGroup;

public interface RoleGroupRepository extends JpaRepository<RoleGroup, Long> {

    @Query("SELECT r.nameRoleGroup FROM RoleGroup r WHERE r.roleGroupId = ?1 AND r.isDeleted = 0")
    String findRoleNameByRoleGroupId(Long roleGroupId);
}
