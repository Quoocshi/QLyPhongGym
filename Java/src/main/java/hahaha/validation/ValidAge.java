package hahaha.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AgeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {
    String message() default "Tuổi phải từ 16 đến 100";
    int min() default 16;
    int max() default 100;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 