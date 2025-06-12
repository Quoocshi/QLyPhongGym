package hahaha.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import hahaha.model.Account;
import hahaha.repository.AccountRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private AccountRepository accountRepository;

    @Override
public void onAuthenticationSuccess(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Authentication authentication) throws IOException {
    String email, username;
    Account acc;
    // Nếu login bằng google                    
    if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
        email = oAuth2User.getAttribute("email");
        acc = accountRepository.findAccountByEmail(email);// tìm bằng mail vì username sau này có thể thay đổi
        if (acc == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không tìm thấy tài khoản Google.");
            return;
        }
        username = acc.getUserName();
    } else { // Nếu login bình thường
        username = authentication.getName();
        acc = accountRepository.findAccountByUserName(username);
        if (acc == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không tìm thấy tài khoản người dùng.");
            return;
        }
    }

    Long accountId = acc.getAccountId();
    String role = acc.getRoleGroup().getNameRoleGroup();
    
    String redirectUrl = switch (role) {
        case "ADMIN" -> "/admin/home/" + accountId + "/" + username;
        case "USER" -> "/user/home/" + accountId + "/" + username;
        case "STAFF" -> "/staff/home/" + accountId + "/" + username;
        case "TRAINER" -> "/trainer/home/" + accountId + "/" + username;
        default -> null;
    };

    if (redirectUrl == null) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không có quyền truy cập.");
        return;
    }

System.out.println("username = " + username);
System.out.println("accountId = " + accountId);
System.out.println("role = " + role);
System.out.println("redirectUrl = " + redirectUrl);

    response.sendRedirect(redirectUrl);
}

}
