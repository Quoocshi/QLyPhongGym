package hahaha.config.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // REST API không dùng session -> stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Public routes (login, register, static resources, etc.)
                        .requestMatchers("/api/auth/**", "/css/**", "/js/**", "/images/**").permitAll()

                        // Role-based access
                        .requestMatchers("/api/autocomplete/**").hasAnyRole("ADMIN", "STAFF", "USER", "TRAINER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/staff/**").hasRole("STAFF")
                        .requestMatchers("/api/user/**").hasRole("USER")
                        .requestMatchers("/api/trainer/**").hasRole("TRAINER")
                        .requestMatchers("/api/quan-ly-nhan-vien/**").hasRole("ADMIN")
                        .requestMatchers("/api/quan-ly-khach-hang/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/quan-ly-dich-vu/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/quan-ly-bo-mon/**").hasRole("ADMIN")
                        .requestMatchers("/api/dich-vu-gym/**").hasRole("USER")
                        .requestMatchers("/api/thanh-toan/**").hasRole("USER")
                        .requestMatchers("/api/vnpay/**").hasRole("USER")

                        // All others require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
