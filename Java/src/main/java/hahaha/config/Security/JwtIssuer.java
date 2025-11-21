package hahaha.config.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtIssuer {
    private final JwtProperties jwtProperties;
    public String issue(Long accountId ,String username, List<String> roles){
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(Date.from(Instant.now().plus(Duration.of(1, ChronoUnit.DAYS))))
                .withClaim("accountId", accountId)
                .withClaim("username", username)
                .withClaim("roles", roles)
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));
    }
}
