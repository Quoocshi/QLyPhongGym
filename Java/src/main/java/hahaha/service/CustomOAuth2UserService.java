package hahaha.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import hahaha.model.Account;
import hahaha.model.RoleGroup;
import hahaha.repository.AccountRepository;
import hahaha.repository.RoleGroupRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleGroupRepository roleGroupRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Account account = accountRepository.findAccountByEmail(email);
        System.out.println("This code is executed when user logs in with Google");
        if (account == null) {
            account = new Account();

            RoleGroup role = roleGroupRepository.findById(3L).orElse(null); // đảm bảo 3L là ROLE_USER
            account.setRoleGroup(role);
            if(role == null) {
                    System.out.println(" ROLE not found - aborting user creation");
                throw new OAuth2AuthenticationException("Role not found");
            }
            account.setEmail(email);
            account.setUserName(email);
            account.setFullName(name);
            account.setPasswordHash("google-auth"); // không cần thật vì OAuth2
            account.setIsDeleted(0);
            account.setStatus("ACTIVE");            // Gán role mặc định: USER


            accountRepository.save(account);
            accountRepository.save(account);
            System.out.println("✅ Account created: " + account.getUserName());
        } else {
            System.out.println(" Dang tim account:"+ account.getUserName());
        }
        // Lấy lại account sau khi lưu
        Account savedAccount = accountRepository.findAccountByEmail(email);
        String role = savedAccount.getRoleGroup().getNameRoleGroup(); // VD: ROLE_USER

        // ✅ Trả về OAuth2User có authority đúng
        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role)),
            oAuth2User.getAttributes(),
            "email"
        );
    }
}
