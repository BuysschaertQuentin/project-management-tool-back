package com.iscod.project_management_tool_back.constraint;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Password format validator.
 * 
 * SECURITY NOTE: This validator only checks password format (pattern matching).
 * For a production-ready secure implementation, you should:
 * - Hash passwords using BCrypt (e.g., Spring Security's PasswordEncoder)
 * - Never store plain text passwords in the database
 * - Implement JWT (JSON Web Token) for stateless authentication
 * - Use HTTPS for all communications
 * - Add rate limiting to prevent brute force attacks
 * - Implement account lockout after failed login attempts
 */
@Component
public class ClassPasswordValidator implements ConstraintValidator<PasswordValidator, String> {
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=/.-]).*$";

    @Override
    public void initialize(PasswordValidator constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        return Pattern.compile(PASSWORD_PATTERN).matcher(password).matches();
    }
}
