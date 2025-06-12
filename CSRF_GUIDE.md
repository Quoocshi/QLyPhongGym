# Hướng dẫn bật CSRF Protection trong Spring Security

## 1. Cấu hình CSRF trong SecurityConfig.java

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            // Sử dụng Cookie-based CSRF token repository
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            
            // Bỏ qua CSRF cho các endpoints không cần thiết
            .ignoringRequestMatchers(
                "/vnpay/return",           // VNPay callback
                "/api/public/**",          // Public APIs
                "/actuator/**"             // Health check endpoints
            )
        )
        // ... rest of configuration
}
```

## 2. Các lựa chọn cấu hình CSRF

### A. Cookie-based CSRF (Khuyến nghị)
```java
.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
```
- CSRF token được lưu trong cookie
- JavaScript có thể đọc token để gửi AJAX requests
- Tự động include token trong forms

### B. Session-based CSRF (Mặc định)
```java
.csrf(Customizer.withDefaults()) // Hoặc không cấu hình gì
```
- CSRF token lưu trong HTTP session
- An toàn hơn nhưng khó sử dụng với SPA/AJAX

### C. Custom CSRF Repository
```java
.csrfTokenRepository(new MyCustomCsrfTokenRepository())
```

## 3. Cập nhật HTML Templates

### A. Thymeleaf Forms (Tự động)
```html
<form th:action="@{/submit}" method="post">
    <!-- Thymeleaf tự động thêm CSRF token -->
    <input type="text" name="data" />
    <button type="submit">Submit</button>
</form>
```

### B. Manual CSRF Token
```html
<form th:action="@{/submit}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
    <input type="text" name="data" />
    <button type="submit">Submit</button>
</form>
```

### C. AJAX Requests với Cookie-based CSRF
```javascript
// Lấy CSRF token từ cookie
function getCsrfToken() {
    return document.cookie
        .split('; ')
        .find(row => row.startsWith('XSRF-TOKEN'))
        ?.split('=')[1];
}

// Gửi AJAX request
$.ajaxSetup({
    beforeSend: function(xhr, settings) {
        if (settings.type == 'POST' || settings.type == 'PUT' || settings.type == 'DELETE') {
            xhr.setRequestHeader("X-XSRF-TOKEN", getCsrfToken());
        }
    }
});
```

## 4. Cấu hình cho các trường hợp đặc biệt

### A. REST APIs
```java
.csrf(csrf -> csrf
    .ignoringRequestMatchers("/api/**")  // Disable CSRF cho REST APIs
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
)
```

### B. Stateless Authentication (JWT)
```java
.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.csrf(csrf -> csrf.disable()) // CSRF không cần thiết với JWT
```

## 5. Testing CSRF

### A. Test với CSRF enabled
```java
@Test
@WithMockUser
void testWithCsrf() throws Exception {
    mockMvc.perform(post("/submit")
            .with(csrf()) // Thêm CSRF token
            .param("data", "test"))
        .andExpect(status().isOk());
}
```

### B. Test CSRF failure
```java
@Test
@WithMockUser
void testCsrfFailure() throws Exception {
    mockMvc.perform(post("/submit")
            .param("data", "test")) // Không có CSRF token
        .andExpect(status().isForbidden());
}
```

## 6. Troubleshooting

### A. CSRF Token Missing
**Lỗi**: 403 Forbidden - CSRF token missing
**Giải pháp**:
- Kiểm tra form có CSRF token
- Verify AJAX requests include token
- Check ignoringRequestMatchers configuration

### B. Invalid CSRF Token
**Lỗi**: 403 Forbidden - Invalid CSRF token
**Giải pháp**:
- Session timeout - user cần login lại
- Token repository configuration issue
- Cookie domain/path mismatch

### C. CSRF với SPA/AJAX
```javascript
// Meta tag approach
<meta name="csrf-token" content="${_csrf.token}">
<meta name="csrf-header" content="${_csrf.headerName}">

// JavaScript
var token = $('meta[name="csrf-token"]').attr('content');
var header = $('meta[name="csrf-header"]').attr('content');

$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
});
```

## 7. Best Practices

1. **Luôn bật CSRF** trừ khi có lý do đặc biệt
2. **Sử dụng HTTPS** trong production
3. **Validate CSRF token** ở server-side
4. **Set proper cookie settings**:
   ```java
   .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
   .and()
   .sessionManagement(session -> session
       .sessionFixation().migrateSession()
       .maximumSessions(1)
       .maxSessionsPreventsLogin(false))
   ```

5. **Monitor CSRF failures** trong logs
6. **Educate users** về browser security 