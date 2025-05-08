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
import hahaha.repository.UserGymRepository;

@Service
public class LoginService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountAssignRoleGroupRepository assignRoleGroupRepository;

    @Autowired
    private RoleGroupRepository roleGroupRepository;
@Autowired
private UserGymRepository userGymRepository;

@Override
public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    Account acc = null;
    //login có thể là username hoặc email
    if (login.contains("@")) {
        // Nếu người dùng nhập email
        Long userId = userGymRepository.findUserIdByEmail(login);
        if (userId == null) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng với email: " + login);
        }

        acc = accountRepository.findByUserId(userId);
    } else {
        // Nếu người dùng nhập username
        acc = accountRepository.findAccountByUsername(login);
    }
    Long roleGroupId = assignRoleGroupRepository.findRoleGroupIdByAccountId(acc.getAccountId());
    if (roleGroupId == null) {
        throw new UsernameNotFoundException("User không có role.");
    }

    String roleName = roleGroupRepository.findRoleNameByRoleGroupId(roleGroupId);
    if (roleName == null) {
        throw new UsernameNotFoundException("Không tìm thấy role name.");
    }

    return User.builder()
            .username(acc.getUsername())
            .password(acc.getPasswordHash()) 
            .authorities("ROLE_" + roleName)
            .build();
}

    

}
