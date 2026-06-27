package com.rsh.fitness_centre.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

/**
 * Validator implementation for PasswordPolicy.
 */
public class PasswordPolicyValidator implements ConstraintValidator<PasswordPolicy, String> {

  private static final Set<String> COMMON_PASSWORDS = Set.of(
      "password",
      "12345678",
      "87654321",
      "qwertyui",
      "qwertyuiop",
      "admin123",
      "password123",
      "welcome123",
      "letmein123"
  );

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    if (password == null) {
      return false;
    }

    // 1. Length check (min 8 chars)
    if (password.length() < 8) {
      return false;
    }

    // 2. Check for common passwords
    if (COMMON_PASSWORDS.contains(password.toLowerCase().trim())) {
      return false;
    }

    // 3. Complexity requirements
    boolean hasUppercase = false;
    boolean hasLowercase = false;
    boolean hasDigit = false;
    boolean hasSpecial = false;

    String specialChars = "!@#$%^&*()_+-=[]{};':\",./<>?|\\~`";

    for (char c : password.toCharArray()) {
      if (Character.isUpperCase(c)) {
        hasUppercase = true;
      } else if (Character.isLowerCase(c)) {
        hasLowercase = true;
      } else if (Character.isDigit(c)) {
        hasDigit = true;
      } else if (specialChars.indexOf(c) >= 0) {
        hasSpecial = true;
      }
    }

    return hasUppercase && hasLowercase && hasDigit && hasSpecial;
  }
}
