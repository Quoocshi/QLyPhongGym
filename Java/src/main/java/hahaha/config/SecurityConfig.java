package hahaha.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import hahaha.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Bật kiểm tra phân quyền
public class SecurityConfig {
    @Autowired
    CustomOAuth2UserService customOAuth2UserService;
    @Autowired
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            //.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers( "/login","/log&re","/register","/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/staff/**").hasRole("STAFF")
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/trainer/**").hasRole("TRAINER")
                .requestMatchers("/quan-ly-nhan-vien/**").hasRole("ADMIN")
                .requestMatchers("/quan-ly-khach-hang/**").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers("/quan-ly-dich-vu/**").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers("/quan-ly-bo-mon/**").hasRole("ADMIN")
                .requestMatchers("/dich-vu-gym/**").hasRole("USER")
                .requestMatchers("/thanh-toan/**").hasRole("USER")
                .requestMatchers("/vnpay/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)) // service để xử lý user info
                .successHandler(customAuthenticationSuccessHandler)) // xử lý sau khi đăng nhập thành công
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customAuthenticationSuccessHandler) 
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );
    
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
