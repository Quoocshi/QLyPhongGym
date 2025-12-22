package hahaha.config.websocket;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import hahaha.config.Security.JwtProperties;
import hahaha.model.Account;
import hahaha.model.CustomUserDetails;
import hahaha.model.NhanVien;
import hahaha.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtWebSocketChannelInterceptor implements ChannelInterceptor {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            // Xử lý CONNECT: lấy JWT và authenticate
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                String authToken = accessor.getFirstNativeHeader("Authorization");

                System.out.println("=== CONNECT attempt ===");
                System.out.println("Authorization header: " + authToken);

                if (authToken != null && authToken.startsWith("Bearer ")) {
                    String token = authToken.substring(7);

                    try {
                        // Validate và decode JWT token (giống JwtAuthenticationFilter)
                        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtProperties.getSecretKey()))
                                .build()
                                .verify(token);

                        String username = jwt.getClaim("username").asString();
                        Long accountId = jwt.getClaim("accountId").asLong();
                        List<String> roles = jwt.getClaim("roles").asList(String.class);
                        if (roles == null) roles = List.of();

                        // Tạo authorities từ roles (thêm ROLE_ prefix nếu chưa có)
                        List<GrantedAuthority> authorities = roles.stream()
                                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                        // Tạo CustomUserDetails
                        Account account = accountRepository.findByAccountId(accountId);

                        CustomUserDetails userDetails = new CustomUserDetails(
                                accountId,
                                username,
                                null,
                                account != null ? account.getNhanVien() : null,
                                account != null ? account.getKhachHang() : null,
                                authorities
                        );

                        // Tạo Authentication object
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                        // QUAN TRỌNG: Set vào user của accessor
                        accessor.setUser(authentication);

                        // Log để debug
                        System.out.println("=== WebSocket CONNECT SUCCESS ===");
                        System.out.println("User: " + username);
                        System.out.println("AccountId: " + accountId);
                        System.out.println("Roles: " + authorities);

                    } catch (Exception e) {
                        System.err.println("=== JWT validation FAILED ===");
                        System.err.println("Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("=== No Authorization header or invalid format ===");
                }
            }

            // Xử lý các message khác: log authentication info
            else {
                Authentication auth = (Authentication) accessor.getUser();
                if (auth != null) {
                    System.out.println("=== WebSocket MESSAGE ===");
                    System.out.println("Command: " + accessor.getCommand());
                    System.out.println("User: " + auth.getName());
                    System.out.println("Authorities: " + auth.getAuthorities());
                } else {
                    System.out.println("=== MESSAGE without authentication ===");
                    System.out.println("Command: " + accessor.getCommand());
                }
            }
        }

        return message;
    }
}