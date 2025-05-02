package hahaha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import hahaha.model.Account;
import hahaha.repository.AccountAssignRoleGroupRepository;
import hahaha.repository.AccountRepository;
import hahaha.repository.RoleGroupRepository;

@Service
public class LoginServiceImpl implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountAssignRoleGroupRepository assignRoleGroupRepository;

    @Autowired
    private RoleGroupRepository roleGroupRepository;

    @Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account acc = accountRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

    Long roleGroupId = assignRoleGroupRepository.findRoleGroupIdByAccountId(acc.getAccountId());
    if (roleGroupId == null) {
        throw new UsernameNotFoundException("User không có role.");
    }

    String roleName = roleGroupRepository.findRoleNameByRoleGroupId(roleGroupId);
    if (roleName == null) {
        throw new UsernameNotFoundException("Không tìm thấy role name.");
    }

    System.out.println("Role name tìm thấy: " + roleName);

    return User.builder()
            .username(acc.getUsername())
            .password(acc.getPasswordHash()) 
            .authorities("ROLE_" + roleName)
            .build();
}

    

}
