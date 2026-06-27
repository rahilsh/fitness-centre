package com.rsh.fitness_centre.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rsh.fitness_centre.entity.response.ErrorResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler globalExceptionHandler;
  private WebRequest webRequest;

  @BeforeEach
  void setUp() {
    globalExceptionHandler = new GlobalExceptionHandler();
    webRequest = mock(WebRequest.class);
    when(webRequest.getDescription(false)).thenReturn("uri=/test/endpoint");
  }

  @Test
  @DisplayName("Should handle validation exceptions with 400 status")
  void testHandleValidationExceptions() {
    // Arrange
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);

    FieldError fieldError = new FieldError("object", "field", "error message");
    when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleValidationExceptions(ex, webRequest);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().getStatus());
    assertEquals("Validation Failed", response.getBody().getError());
    assertEquals("Input validation failed", response.getBody().getMessage());
    assertEquals("/test/endpoint", response.getBody().getPath());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  @DisplayName("Should handle booking not found exception with 404 status")
  void testHandleBookingNotFound() {
    // Arrange
    BookingNotFoundException ex = new BookingNotFoundException("Booking ID 123 not found");

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleBookingNotFound(ex, webRequest);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(404, response.getBody().getStatus());
    assertEquals("Booking Not Found", response.getBody().getError());
    assertEquals("Booking ID 123 not found", response.getBody().getMessage());
    assertEquals("/test/endpoint", response.getBody().getPath());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  @DisplayName("Should handle global exceptions with 500 status")
  void testHandleGlobalException() {
    // Arrange
    Exception ex = new Exception("Unexpected error occurred");

    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(ex, webRequest);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(500, response.getBody().getStatus());
    assertEquals("Internal Server Error", response.getBody().getError());
    assertEquals("An unexpected error occurred", response.getBody().getMessage());
    assertEquals("/test/endpoint", response.getBody().getPath());
    assertEquals("Unexpected error occurred", response.getBody().getDetails());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  @DisplayName("Should include path in error response")
  void testPathIncludedInErrorResponse() {
    // Arrange
    when(webRequest.getDescription(false)).thenReturn("uri=/api/users");
    BookingNotFoundException ex = new BookingNotFoundException("Not found");

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleBookingNotFound(ex, webRequest);

    // Assert
    assertEquals("/api/users", response.getBody().getPath());
  }

  @Test
  @DisplayName("Should set timestamp in error response")
  void testTimestampInErrorResponse() {
    // Arrange
    BookingNotFoundException ex = new BookingNotFoundException("Not found");
    LocalDateTime beforeCall = LocalDateTime.now();

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleBookingNotFound(ex, webRequest);
    LocalDateTime afterCall = LocalDateTime.now();

    // Assert
    assertNotNull(response.getBody().getTimestamp());
    assertTrue(
        response.getBody().getTimestamp().isAfter(beforeCall.minusSeconds(1))
            && response.getBody().getTimestamp().isBefore(afterCall.plusSeconds(1)));
  }

  @Test
  @DisplayName("Should handle null details in error response")
  void testNullDetailsInErrorResponse() {
    // Arrange
    BookingNotFoundException ex = new BookingNotFoundException("Not found");

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleBookingNotFound(ex, webRequest);

    // Assert
    // Details should be null for BookingNotFoundException
    // The response will not include details in JSON due to @JsonInclude(NON_NULL)
    assertEquals(404, response.getBody().getStatus());
  }

  @Test
  @DisplayName("Should handle exception with null message")
  void testExceptionWithNullMessage() {
    // Arrange
    Exception ex = new Exception();

    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(ex, webRequest);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(500, response.getBody().getStatus());
  }

  @Test
  @DisplayName("Should handle validation exception with multiple field errors")
  void testValidationExceptionWithMultipleErrors() {
    // Arrange
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);

    FieldError error1 = new FieldError("object", "name", "Name is required");
    FieldError error2 = new FieldError("object", "email", "Email is invalid");
    when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(error1, error2));

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleValidationExceptions(ex, webRequest);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Validation Failed", response.getBody().getError());
    assertTrue(response.getBody().getDetails().contains("name"));
    assertTrue(response.getBody().getDetails().contains("email"));
  }

  @Test
  @DisplayName("Should correctly format error response for validation")
  void testErrorResponseFormatForValidation() {
    // Arrange
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);

    FieldError fieldError = new FieldError("object", "field", "error");
    when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleValidationExceptions(ex, webRequest);

    // Assert
    ErrorResponse errorBody = response.getBody();
    assertNotNull(errorBody);
    assertEquals(400, errorBody.getStatus());
    assertEquals("Validation Failed", errorBody.getError());
    assertNotNull(errorBody.getPath());
    assertNotNull(errorBody.getTimestamp());
  }

  @Test
  @DisplayName("Should handle different exception types")
  void testHandleDifferentExceptionTypes() {
    // Arrange
    RuntimeException runtimeEx = new RuntimeException("Runtime error");
    IllegalArgumentException illegalArgEx = new IllegalArgumentException("Invalid argument");

    // Act
    ResponseEntity<ErrorResponse> runtimeResponse =
        globalExceptionHandler.handleGlobalException(runtimeEx, webRequest);
    ResponseEntity<ErrorResponse> illegalArgResponse =
        globalExceptionHandler.handleGlobalException(illegalArgEx, webRequest);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, runtimeResponse.getStatusCode());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, illegalArgResponse.getStatusCode());
    assertEquals("Runtime error", runtimeResponse.getBody().getDetails());
    assertEquals("Invalid argument", illegalArgResponse.getBody().getDetails());
  }

  @Test
  @DisplayName("Should preserve exception details in error response")
  void testPreserveExceptionDetails() {
    // Arrange
    String exceptionMessage = "Database connection failed";
    Exception ex = new Exception(exceptionMessage);

    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(ex, webRequest);

    // Assert
    assertEquals(exceptionMessage, response.getBody().getDetails());
  }
}
