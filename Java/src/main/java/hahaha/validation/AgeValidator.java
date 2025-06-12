package hahaha.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class AgeValidator implements ConstraintValidator<ValidAge, LocalDate> {
    
    private int min;
    private int max;
    
    @Override
    public void initialize(ValidAge constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }
    
    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return true; // Let @NotNull handle null values
        }
        
        LocalDate now = LocalDate.now();
        int age = Period.between(birthDate, now).getYears();
        
        return age >= min && age <= max;
    }
} 