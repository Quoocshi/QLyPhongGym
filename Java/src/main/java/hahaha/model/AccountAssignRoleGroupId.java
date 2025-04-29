package hahaha.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AccountAssignRoleGroupId implements Serializable {

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "ROLE_GROUP_ID")
    private Long roleGroupId;

    // Constructors
    public AccountAssignRoleGroupId() {}

    public AccountAssignRoleGroupId(Long accountId, Long roleGroupId) {
        this.accountId = accountId;
        this.roleGroupId = roleGroupId;
    }

    // Getters and Setters
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getRoleGroupId() {
        return roleGroupId;
    }

    public void setRoleGroupId(Long roleGroupId) {
        this.roleGroupId = roleGroupId;
    }

    // hashCode and equals bắt buộc
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountAssignRoleGroupId)) return false;
        AccountAssignRoleGroupId that = (AccountAssignRoleGroupId) o;
        return Objects.equals(accountId, that.accountId) &&
               Objects.equals(roleGroupId, that.roleGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, roleGroupId);
    }
}
