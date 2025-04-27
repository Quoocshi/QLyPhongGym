package hahaha.model;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
@Entity
@Table(name = "ACCOUNT_ASSIGN_ROLE_GROUP")
public class AccountAssignRoleGroup {

    @EmbeddedId
    private AccountAssignRoleGroupId id;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "IS_DELETED")
    private Integer isDeleted;

    public Long getAccountId() {
        return id.getAccountId();
    }

    public Long getRoleGroupId() {
        return id.getRoleGroupId();
    }

    // Getters and Setters
    public AccountAssignRoleGroupId getId() {
        return id;
    }

    public void setId(AccountAssignRoleGroupId id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }
}
    
        