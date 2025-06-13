package hahaha.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý lỗi validation cho @Valid trên method parameters
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex,
                                           RedirectAttributes redirectAttributes,
                                           HttpServletRequest request) {
        
        BindingResult bindingResult = ex.getBindingResult();
        
        // Lấy lỗi đầu tiên để hiển thị
        if (bindingResult.hasErrors()) {
            FieldError firstError = bindingResult.getFieldErrors().get(0);
            String errorMessage = firstError.getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMessage", "❌ " + errorMessage);
            
            // Log để debug
            System.out.println("❌ MethodArgumentNotValidException: " + errorMessage);
        }
        
        // Kiểm tra URL để redirect đúng trang
        String requestURL = request.getRequestURI();
        if (requestURL.contains("/register")) {
            return "redirect:/register";
        }
        
        return "redirect:/login";
    }

    /**
     * Xử lý lỗi validation cho @Validated trên class level
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(ConstraintViolationException ex,
                                                   RedirectAttributes redirectAttributes,
                                                   HttpServletRequest request) {
        
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        redirectAttributes.addFlashAttribute("errorMessage", "❌ " + errorMessage);
        
        // Log để debug
        System.out.println("❌ ConstraintViolationException: " + errorMessage);
        
        // Kiểm tra URL để redirect đúng trang  
        String requestURL = request.getRequestURI();
        if (requestURL.contains("/register")) {
            return "redirect:/register";
        }
        
        return "redirect:/login";
    }

    /**
     * Xử lý lỗi static resource - không redirect để tránh loop
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(NoResourceFoundException ex,
                                               HttpServletRequest request) {
        // Log lỗi nhưng không redirect để tránh loop
        System.err.println("❌ Static resource not found: " + ex.getMessage());
        
        // Trả về error page thay vì redirect
        return "error/404";
    }

    /**
     * Xử lý các exception chung khác (trừ static resource)
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, 
                                       RedirectAttributes redirectAttributes,
                                       HttpServletRequest request) {
        
        // Bỏ qua các lỗi static resource để tránh redirect loop
        if (ex instanceof NoResourceFoundException) {
            return "error/404";
        }
        
        // Log lỗi cho developer
        System.err.println("❌ Unexpected error: " + ex.getMessage());
        ex.printStackTrace();
        
        // Hiển thị thông báo lỗi an toàn cho user
        redirectAttributes.addFlashAttribute("errorMessage", "❌ Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
        
        // Kiểm tra URL để redirect đúng trang
        String requestURL = request.getRequestURI();
        if (requestURL.contains("/register")) {
            return "redirect:/register";
        }
        
        return "redirect:/login";
    }
} 