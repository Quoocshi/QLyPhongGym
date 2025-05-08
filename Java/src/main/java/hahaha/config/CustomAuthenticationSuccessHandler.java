package hahaha.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import hahaha.model.Account;
import hahaha.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private AccountRepository accountRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = null; 
        String username = authentication.getName();
        Account acc = accountRepository.findAccountByUsername(username);
        Long userID = acc.getUserId();
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (null != role) {
                switch (role) {
                    case "ROLE_ADMIN" -> redirectUrl = "/admin/home/"+ userID + "/" + username;
                    case "ROLE_USER" -> redirectUrl = "/user/home/" + userID + "/" + username;
                    case "ROLE_STAFF" -> redirectUrl = "/staff/home/" + userID + "/" + username;
                    default -> {
                    }
                }
            }
        }
        if (redirectUrl == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không có quyền truy cập");
            return;
        }

        response.sendRedirect(redirectUrl);
    }
}
