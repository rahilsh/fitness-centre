package com.rsh.fitness_centre.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for enforcing strong password policies.
 */
@Documented
@Constraint(validatedBy = PasswordPolicyValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordPolicy {

  String message() default "Password does not meet complexity requirements: " +
      "must be at least 8 characters long, contain at least one uppercase letter, " +
      "one lowercase letter, one digit, one special character, and must not be a common password.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
