package hahaha.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/login"); // Định nghĩa API login mới
    }

    @Override
public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    try {
        Map<String, String> loginRequest = new ObjectMapper().readValue(request.getInputStream(), Map.class);
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        UsernamePasswordAuthenticationToken authRequest =
            new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>()); // <-- FIX Ở ĐÂY

        return authenticationManager.authenticate(authRequest);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}



@Override
protected void successfulAuthentication(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain chain,
                                        Authentication authResult) throws IOException, ServletException {
    // Gán vào SecurityContext
    SecurityContextHolder.getContext().setAuthentication(authResult);

    // TỰ TẠO session và gán SecurityContext
    request.getSession(true) // create session if not exists
            .setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    Map<String, Object> result = new HashMap<>();
    result.put("username", authResult.getName());
    result.put("role", authResult.getAuthorities().stream().findFirst().get().getAuthority());

    new ObjectMapper().writeValue(response.getWriter(), result);
}



    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> result = new HashMap<>();
        result.put("error", "Sai tên đăng nhập hoặc mật khẩu");

        new ObjectMapper().writeValue(response.getWriter(), result);
    }
}
