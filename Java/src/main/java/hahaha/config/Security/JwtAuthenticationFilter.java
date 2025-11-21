package hahaha.config.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import hahaha.model.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

    public JwtAuthenticationFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtProperties.getSecretKey()))
                    .build()
                    .verify(token);

            String username = jwt.getClaim("username").asString();
            Long accountId = jwt.getClaim("accountId").asLong();
            List<String> roles = jwt.getClaim("roles").asList(String.class);
            if (roles == null) roles = List.of();

            //Tạo authorities từ roles
            List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            //Tạo CustomUserDetails, không cần giữ lại roles string nữa
            CustomUserDetails userDetails = new CustomUserDetails(accountId, username, null, authorities);

            //Tạo Authentication object
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Lưu accountId vào request attribute để controller dùng
            request.setAttribute("accountId", accountId);

        } catch (Exception e) {
            // token không hợp lệ
            System.out.println("JWT invalid: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
