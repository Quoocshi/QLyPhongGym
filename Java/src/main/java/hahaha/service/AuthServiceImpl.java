package hahaha.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import hahaha.model.Account;
import hahaha.repository.AccountRepository;

@Service
public class AuthServiceImpl implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🧪 username nhập từ form: '" + username + "'");
        Account acc = accountRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("⚠ Không tìm thấy user: " + username);
                    return new UsernameNotFoundException("Không tìm thấy user");
                });

        return User.builder()
                .username(acc.getUsername())
                .password(acc.getPasswordHash())
                .roles("USER")
                .build();
    }
}
