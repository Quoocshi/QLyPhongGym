package hahaha.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import hahaha.model.Account;
import hahaha.repository.AccountRepository;
import hahaha.repository.RoleGroupRepository;

@Service
public class LoginService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleGroupRepository roleGroupRepository;

@Override
public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    Account acc = null;
    //login có thể là username hoặc email
    if (login.contains("@")) {
        // Nếu người dùng nhập email
        Long accountId = accountRepository.findAccountIdByEmail(login);
        if (accountId == null) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng với email: " + login);
        }

        acc = accountRepository.findByAccountId(accountId);
    } else {
        // Nếu người dùng nhập username
        acc = accountRepository.findAccountByUserName(login);
    }
    if (acc == null) {
        throw new UsernameNotFoundException("Không tìm thấy người dùng với username: " + login);
    }

    Long roleGroupId = accountRepository.findRoleGroupIdByAccountId(acc.getAccountId());
    if (roleGroupId == null) {
        throw new UsernameNotFoundException("User không có role.");
    }

    String roleName = roleGroupRepository.findRoleNameByRoleGroupId(roleGroupId);
    if (roleName == null) {
        throw new UsernameNotFoundException("Không tìm thấy role name.");
    }

    System.out.println(">>> Đăng nhập thành công với username: " + acc.getUserName());
    System.out.println(">>> Role: ROLE_" + roleName);

    return User.builder()
            .username(acc.getUserName())
            .password(acc.getPasswordHash()) 
            .authorities("ROLE_" + roleName)
            .build();
}

    

}
