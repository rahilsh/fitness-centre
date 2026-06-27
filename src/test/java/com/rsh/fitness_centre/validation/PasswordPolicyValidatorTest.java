package com.rsh.fitness_centre.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for PasswordPolicyValidator.
 */
class PasswordPolicyValidatorTest {

  private PasswordPolicyValidator validator;

  @BeforeEach
  void setUp() {
    validator = new PasswordPolicyValidator();
  }

  @Test
  @DisplayName("Should accept valid strong password")
  void testValidPassword() {
    assertTrue(validator.isValid("SecurePassword123!", null));
    assertTrue(validator.isValid("Strong#Pass99", null));
  }

  @Test
  @DisplayName("Should reject password shorter than 8 characters")
  void testShortPassword() {
    assertFalse(validator.isValid("P@ss1", null));
  }

  @Test
  @DisplayName("Should reject password without uppercase character")
  void testNoUppercase() {
    assertFalse(validator.isValid("securepassword123!", null));
  }

  @Test
  @DisplayName("Should reject password without lowercase character")
  void testNoLowercase() {
    assertFalse(validator.isValid("SECUREPASSWORD123!", null));
  }

  @Test
  @DisplayName("Should reject password without digit")
  void testNoDigit() {
    assertFalse(validator.isValid("SecurePassword!", null));
  }

  @Test
  @DisplayName("Should reject password without special character")
  void testNoSpecial() {
    assertFalse(validator.isValid("SecurePassword123", null));
  }

  @Test
  @DisplayName("Should reject common passwords")
  void testCommonPasswords() {
    assertFalse(validator.isValid("password", null));
    assertFalse(validator.isValid("password123", null));
    assertFalse(validator.isValid("12345678", null));
    assertFalse(validator.isValid("qwertyuiop", null));
  }

  @Test
  @DisplayName("Should reject null password")
  void testNullPassword() {
    assertFalse(validator.isValid(null, null));
  }
}
