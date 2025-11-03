package hahaha.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class RegisterResponse {
    private String message;
    private String username;
    private String role;
    private LocalDateTime createdAt;
}

