# 🛡️ Hướng dẫn khắc phục Input Validation THIẾU

## ✅ ĐÃ HOÀN THÀNH:
1. ✅ Thêm validation cho RegisterForm
2. ✅ Cập nhật RegisterController với @Valid
3. ✅ Thêm validation error display trong registration template
4. ✅ Thêm validation cho NhanVien model
5. ✅ Tạo GlobalExceptionHandler
6. ✅ Tạo custom validation annotations (@ValidAge)

## 🔧 CẦN LÀM TIẾP:

### **1. Models cần thêm validation:**

#### **A. KhachHang.java**
```java
@NotBlank(message = "Mã khách hàng không được để trống")
@Pattern(regexp = "^KH\\d{3,}$", message = "Mã khách hàng phải có định dạng KH###")
private String maKH;

@NotBlank(message = "Họ tên không được để trống")
@Size(min = 2, max = 100, message = "Họ tên phải từ 2-100 ký tự")
@Pattern(regexp = "^[\\p{L}\\s]+$", message = "Họ tên chỉ chứa chữ cái")
private String hoTen;

@NotBlank(message = "Email không được để trống")
@Email(message = "Email không đúng định dạng")
private String email;

@NotBlank(message = "Số điện thoại không được để trống")
@Pattern(regexp = "^(\\+84|0)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ")
private String soDienThoai;

@NotNull(message = "Ngày sinh không được để trống")
@Past(message = "Ngày sinh phải là ngày trong quá khứ")
@ValidAge(min = 16, max = 100, message = "Tuổi phải từ 16-100")
private LocalDate ngaySinh;
```

#### **B. DichVu.java**
```java
@NotBlank(message = "Mã dịch vụ không được để trống")
@Pattern(regexp = "^DV\\d{2,}$", message = "Mã dịch vụ phải có định dạng DV##")
private String maDV;

@NotBlank(message = "Tên dịch vụ không được để trống")
@Size(min = 5, max = 100, message = "Tên dịch vụ phải từ 5-100 ký tự")
private String tenDV;

@NotNull(message = "Thời hạn không được để trống")
@Min(value = 1, message = "Thời hạn phải ít nhất 1 ngày")
@Max(value = 365, message = "Thời hạn không được quá 365 ngày")
private Integer thoiHan;

@NotNull(message = "Đơn giá không được để trống")
@DecimalMin(value = "10000", message = "Đơn giá phải ít nhất 10,000 VNĐ")
@DecimalMax(value = "50000000", message = "Đơn giá không được quá 50,000,000 VNĐ")
private Double donGia;
```

### **2. Controllers cần cập nhật:**

#### **A. QlyNhanVienController.java**
```java
@PostMapping("/them-nhan-vien")
@PreAuthorize("hasRole('ADMIN')")
public String themNhanVien(@Valid NhanVien nhanVien,
                          BindingResult bindingResult,
                          @RequestParam("rawPassword") @Size(min = 6, message = "Mật khẩu phải ít nhất 6 ký tự") String rawPassword,
                          @RequestParam("confirmPassword") String confirmPassword,
                          RedirectAttributes redirectAttributes) {
    
    if (bindingResult.hasErrors()) {
        redirectAttributes.addFlashAttribute("errorMessage", 
            bindingResult.getFieldError().getDefaultMessage());
        return "redirect:/quan-ly-nhan-vien/them-nhan-vien";
    }
    
    // Custom validation
    if (!rawPassword.equals(confirmPassword)) {
        redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu xác nhận không khớp");
        return "redirect:/quan-ly-nhan-vien/them-nhan-vien";
    }
    
    // Rest of implementation...
}
```

#### **B. QlyDichVuController.java**
```java
@PostMapping("/them-dich-vu")
@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
public String themDichVu(@Valid DichVu dichVu,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes) {
    
    if (bindingResult.hasErrors()) {
        redirectAttributes.addFlashAttribute("errorMessage", 
            bindingResult.getFieldError().getDefaultMessage());
        return "redirect:/quan-ly-dich-vu/them-dich-vu";
    }
    
    // Rest of implementation...
}
```

### **3. Request Parameter Validation:**

#### **A. Validate Path Variables**
```java
@GetMapping("/user/{accountId}")
public String userProfile(@PathVariable @Min(1) @Max(999999) Long accountId) {
    // Implementation...
}
```

#### **B. Validate Query Parameters**
```java
@GetMapping("/search")
public String search(@RequestParam @NotBlank @Size(min = 2, max = 50) String keyword) {
    // Implementation...
}
```

### **4. Template Validation Error Display:**

#### **Template pattern để thêm vào mọi form:**
```html
<!-- Error display cho từng field -->
<div class="form-group">
    <label for="fieldName">Field Label</label>
    <input type="text" th:field="*{fieldName}" />
    <div th:if="${#fields.hasErrors('fieldName')}" class="error-message">
        <span th:errors="*{fieldName}"></span>
    </div>
</div>
```

#### **CSS cho validation errors:**
```css
.error-message {
    color: #dc3545;
    font-size: 12px;
    margin-top: 5px;
    display: block;
}

.form-input.error {
    border-color: #dc3545;
    box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
}
```

### **5. Custom Validation Groups:**

```java
// Validation groups for different scenarios
public interface CreateGroup {}
public interface UpdateGroup {}

// In model
@NotNull(groups = CreateGroup.class)
@Null(groups = UpdateGroup.class, message = "ID không được thay đổi khi cập nhật")
private Long id;

// In controller
@PostMapping("/create")
public String create(@Validated(CreateGroup.class) MyModel model) { }

@PostMapping("/update")
public String update(@Validated(UpdateGroup.class) MyModel model) { }
```

### **6. Client-side Validation (JavaScript):**

```javascript
// Validation cho registration form
function validateForm() {
    const userName = document.getElementById('userName').value;
    const password = document.getElementById('password').value;
    const email = document.getElementById('email').value;
    
    // Username validation
    if (!/^[a-zA-Z0-9._-]{3,50}$/.test(userName)) {
        showError('userName', 'Tên đăng nhập không hợp lệ');
        return false;
    }
    
    // Password validation
    if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/.test(password)) {
        showError('password', 'Mật khẩu phải có ít nhất 6 ký tự, 1 chữ hoa, 1 chữ thường và 1 số');
        return false;
    }
    
    // Email validation
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        showError('email', 'Email không đúng định dạng');
        return false;
    }
    
    return true;
}

function showError(fieldId, message) {
    const field = document.getElementById(fieldId);
    const errorDiv = field.parentNode.querySelector('.error-message');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    }
    field.classList.add('error');
}
```

### **7. Security Best Practices:**

#### **A. Input Sanitization**
```java
// Utility class for input cleaning
@Component
public class InputSanitizer {
    
    public String sanitizeString(String input) {
        if (input == null) return null;
        return input.trim()
                   .replaceAll("\\s+", " ")  // Multiple spaces to single
                   .replaceAll("[<>\"'&]", ""); // Remove potential XSS chars
    }
    
    public String sanitizeEmail(String email) {
        if (email == null) return null;
        return email.toLowerCase().trim();
    }
}
```

#### **B. Rate Limiting cho Registration**
```java
@Component
public class RegistrationRateLimiter {
    private final Map<String, List<LocalDateTime>> attempts = new ConcurrentHashMap<>();
    
    public boolean isAllowed(String clientIP) {
        List<LocalDateTime> userAttempts = attempts.computeIfAbsent(clientIP, k -> new ArrayList<>());
        
        // Remove old attempts (older than 1 hour)
        userAttempts.removeIf(time -> time.isBefore(LocalDateTime.now().minusHours(1)));
        
        // Check if exceeded limit (5 attempts per hour)
        return userAttempts.size() < 5;
    }
    
    public void recordAttempt(String clientIP) {
        attempts.computeIfAbsent(clientIP, k -> new ArrayList<>()).add(LocalDateTime.now());
    }
}
```

## 🧪 **Testing Validation:**

### **Unit Tests**
```java
@Test
void testValidation() {
    RegisterForm form = new RegisterForm();
    form.setUserName("ab"); // Too short
    
    Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream()
        .anyMatch(v -> v.getMessage().contains("3-50 ký tự")));
}
```

### **Integration Tests**
```java
@Test
@WithMockUser
void testRegisterWithInvalidData() throws Exception {
    mockMvc.perform(post("/register")
            .param("userName", "ab")
            .param("password", "123")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(model().hasErrors());
}
```

## 📋 **Checklist hoàn thành:**

- [ ] Thêm validation cho tất cả models
- [ ] Cập nhật tất cả controllers với @Valid
- [ ] Thêm error display cho tất cả templates
- [ ] Implement custom validators
- [ ] Add client-side validation
- [ ] Create comprehensive tests
- [ ] Document validation rules

## ⚠️ **Security Notes:**

1. **Server-side validation là bắt buộc** - Client-side chỉ là UX improvement
2. **Sanitize tất cả inputs** trước khi lưu database
3. **Log validation failures** để detect attacks
4. **Rate limiting** cho forms nhạy cảm
5. **Validate file uploads** nếu có
6. **Check business logic constraints** ngoài basic validation 