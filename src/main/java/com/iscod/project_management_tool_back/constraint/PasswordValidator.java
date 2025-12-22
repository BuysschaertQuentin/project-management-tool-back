package com.iscod.project_management_tool_back.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = com.iscod.project_management_tool_back.constraint.ClassPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordValidator {
    String message() default "Mot de passe invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}