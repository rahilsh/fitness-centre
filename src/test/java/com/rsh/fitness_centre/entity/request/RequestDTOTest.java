package com.rsh.fitness_centre.entity.request;

import com.rsh.fitness_centre.entity.Activity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for request DTOs - AddUserRequest, AddBookingRequest, AddFitnessCentreRequest, AddActivityRequest, RegisterRequest, LoginRequest.
 */
@DisplayName("Request DTO Tests")
class RequestDTOTest {

  // ==================== AddUserRequest Tests ====================

  @Test
  @DisplayName("AddUserRequest - Should create with valid name")
  void testAddUserRequestValidName() {
    AddUserRequest request = new AddUserRequest();
    request.setName("John Doe");

    assertEquals("John Doe", request.getName());
  }

  @Test
  @DisplayName("AddUserRequest - Should handle getters and setters")
  void testAddUserRequestGettersSetters() {
    AddUserRequest request = new AddUserRequest();
    String name = "Jane Smith";
    request.setName(name);

    assertEquals(name, request.getName());
  }

  @Test
  @DisplayName("AddUserRequest - Equals method with same name")
  void testAddUserRequestEquals() {
    AddUserRequest request1 = new AddUserRequest();
    request1.setName("John Doe");

    AddUserRequest request2 = new AddUserRequest();
    request2.setName("John Doe");

    assertEquals(request1, request2);
  }

  @Test
  @DisplayName("AddUserRequest - Equals method with different names")
  void testAddUserRequestNotEquals() {
    AddUserRequest request1 = new AddUserRequest();
    request1.setName("John Doe");

    AddUserRequest request2 = new AddUserRequest();
    request2.setName("Jane Smith");

    assertNotEquals(request1, request2);
  }

  @Test
  @DisplayName("AddUserRequest - HashCode same for equal objects")
  void testAddUserRequestHashCode() {
    AddUserRequest request1 = new AddUserRequest();
    request1.setName("John Doe");

    AddUserRequest request2 = new AddUserRequest();
    request2.setName("John Doe");

    assertEquals(request1.hashCode(), request2.hashCode());
  }

  // ==================== AddBookingRequest Tests ====================

  @Test
  @DisplayName("AddBookingRequest - Should create with valid slot and user IDs")
  void testAddBookingRequestValidIds() {
    AddBookingRequest request = new AddBookingRequest();
    request.setSlotId(1L);
    request.setUserId(1L);

    assertEquals(1L, request.getSlotId());
    assertEquals(1L, request.getUserId());
  }

  @Test
  @DisplayName("AddBookingRequest - Should handle getters and setters for slotId")
  void testAddBookingRequestSlotIdGetterSetter() {
    AddBookingRequest request = new AddBookingRequest();
    request.setSlotId(5L);

    assertEquals(5L, request.getSlotId());
  }

  @Test
  @DisplayName("AddBookingRequest - Should handle getters and setters for userId")
  void testAddBookingRequestUserIdGetterSetter() {
    AddBookingRequest request = new AddBookingRequest();
    request.setUserId(10L);

    assertEquals(10L, request.getUserId());
  }

  @Test
  @DisplayName("AddBookingRequest - Should handle multiple updates")
  void testAddBookingRequestMultipleUpdates() {
    AddBookingRequest request = new AddBookingRequest();
    request.setSlotId(1L);
    request.setUserId(1L);

    request.setSlotId(2L);
    request.setUserId(2L);

    assertEquals(2L, request.getSlotId());
    assertEquals(2L, request.getUserId());
  }

  // ==================== AddFitnessCentreRequest Tests ====================

  @Test
  @DisplayName("AddFitnessCentreRequest - Should create with valid name")
  void testAddFitnessCentreRequestValidName() {
    AddFitnessCentreRequest request = new AddFitnessCentreRequest();
    request.setName("FitZone Gym");

    assertEquals("FitZone Gym", request.getName());
  }

  @Test
  @DisplayName("AddFitnessCentreRequest - Should handle timings")
  void testAddFitnessCentreRequestTimings() {
    AddFitnessCentreRequest request = new AddFitnessCentreRequest();
    Set<List<Integer>> timings = new HashSet<>();
    timings.add(Arrays.asList(6, 22));
    timings.add(Arrays.asList(8, 20));
    request.setTimings(timings);

    assertEquals(timings, request.getTimings());
    assertEquals(2, request.getTimings().size());
  }

  @Test
  @DisplayName("AddFitnessCentreRequest - Should handle supported activities")
  void testAddFitnessCentreRequestSupportedActivities() {
    AddFitnessCentreRequest request = new AddFitnessCentreRequest();
    Set<String> activities = new HashSet<>(Arrays.asList("CARDIO", "YOGA", "STRENGTH"));
    request.setSupportedActivities(activities);

    assertEquals(activities, request.getSupportedActivities());
    assertEquals(3, request.getSupportedActivities().size());
  }

  @Test
  @DisplayName("AddFitnessCentreRequest - Should handle empty timings set")
  void testAddFitnessCentreRequestEmptyTimings() {
    AddFitnessCentreRequest request = new AddFitnessCentreRequest();
    request.setTimings(new HashSet<>());

    assertNotNull(request.getTimings());
    assertEquals(0, request.getTimings().size());
  }

  @Test
  @DisplayName("AddFitnessCentreRequest - Should handle all properties together")
  void testAddFitnessCentreRequestAllProperties() {
    AddFitnessCentreRequest request = new AddFitnessCentreRequest();
    request.setName("Premium Fitness");

    Set<List<Integer>> timings = new HashSet<>();
    timings.add(Arrays.asList(7, 21));
    request.setTimings(timings);

    Set<String> activities = new HashSet<>(Arrays.asList("YOGA", "PILATES"));
    request.setSupportedActivities(activities);

    assertEquals("Premium Fitness", request.getName());
    assertEquals(timings, request.getTimings());
    assertEquals(activities, request.getSupportedActivities());
  }

  // ==================== AddActivityRequest Tests ====================

  @Test
  @DisplayName("AddActivityRequest - Should create with valid activity and times")
  void testAddActivityRequestValid() {
    AddActivityRequest request = new AddActivityRequest();
    request.setActivity(Activity.YOGA);
    request.setStartTime(9);
    request.setEndTime(10);
    request.setNoOfSlots(20);

    assertEquals(Activity.YOGA, request.getActivity());
    assertEquals(9, request.getStartTime());
    assertEquals(10, request.getEndTime());
    assertEquals(20, request.getNoOfSlots());
  }

  @Test
  @DisplayName("AddActivityRequest - Should handle getters and setters for activity")
  void testAddActivityRequestActivityGetterSetter() {
    AddActivityRequest request = new AddActivityRequest();
    request.setActivity(Activity.CARDIO);

    assertEquals(Activity.CARDIO, request.getActivity());
  }

  @Test
  @DisplayName("AddActivityRequest - Should handle getters and setters for times")
  void testAddActivityRequestTimesGetterSetter() {
    AddActivityRequest request = new AddActivityRequest();
    request.setStartTime(14);
    request.setEndTime(16);

    assertEquals(14, request.getStartTime());
    assertEquals(16, request.getEndTime());
  }

  @Test
  @DisplayName("AddActivityRequest - Should handle getters and setters for slots")
  void testAddActivityRequestSlotsGetterSetter() {
    AddActivityRequest request = new AddActivityRequest();
    request.setNoOfSlots(50);

    assertEquals(50, request.getNoOfSlots());
  }

  @Test
  @DisplayName("AddActivityRequest - Should allow multiple updates")
  void testAddActivityRequestMultipleUpdates() {
    AddActivityRequest request = new AddActivityRequest();
    request.setActivity(Activity.YOGA);
    request.setStartTime(9);
    request.setEndTime(10);
    request.setNoOfSlots(20);

    // Update
    request.setActivity(Activity.WEIGHTS);
    request.setStartTime(15);
    request.setEndTime(17);
    request.setNoOfSlots(30);

    assertEquals(Activity.WEIGHTS, request.getActivity());
    assertEquals(15, request.getStartTime());
    assertEquals(17, request.getEndTime());
    assertEquals(30, request.getNoOfSlots());
  }

  // ==================== RegisterRequest Tests ====================

  @Test
  @DisplayName("RegisterRequest - Should create with valid data")
  void testRegisterRequestValid() {
    RegisterRequest request = new RegisterRequest("john@example.com", "John Doe", "SecurePass123!", "SecurePass123!");

    assertEquals("john@example.com", request.getEmail());
    assertEquals("John Doe", request.getName());
    assertEquals("SecurePass123!", request.getPassword());
    assertEquals("SecurePass123!", request.getPasswordConfirmation());
  }

  @Test
  @DisplayName("RegisterRequest - Should handle no-arg constructor")
  void testRegisterRequestNoArgConstructor() {
    RegisterRequest request = new RegisterRequest();
    assertNotNull(request);
  }

  @Test
  @DisplayName("RegisterRequest - Should handle all-arg constructor")
  void testRegisterRequestAllArgConstructor() {
    RegisterRequest request = new RegisterRequest("test@example.com", "Test User", "Password123", "Password123");

    assertEquals("test@example.com", request.getEmail());
    assertEquals("Test User", request.getName());
    assertEquals("Password123", request.getPassword());
    assertEquals("Password123", request.getPasswordConfirmation());
  }

  @Test
  @DisplayName("RegisterRequest - Should handle getters and setters for email")
  void testRegisterRequestEmailGetterSetter() {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("newemail@example.com");

    assertEquals("newemail@example.com", request.getEmail());
  }

  @Test
  @DisplayName("RegisterRequest - Should handle getters and setters for name")
  void testRegisterRequestNameGetterSetter() {
    RegisterRequest request = new RegisterRequest();
    request.setName("New User");

    assertEquals("New User", request.getName());
  }

  @Test
  @DisplayName("RegisterRequest - Should handle getters and setters for password")
  void testRegisterRequestPasswordGetterSetter() {
    RegisterRequest request = new RegisterRequest();
    request.setPassword("NewPassword123");

    assertEquals("NewPassword123", request.getPassword());
  }

  @Test
  @DisplayName("RegisterRequest - Should handle getters and setters for passwordConfirmation")
  void testRegisterRequestPasswordConfirmationGetterSetter() {
    RegisterRequest request = new RegisterRequest();
    request.setPasswordConfirmation("ConfirmPass123");

    assertEquals("ConfirmPass123", request.getPasswordConfirmation());
  }

  // ==================== LoginRequest Tests ====================

  @Test
  @DisplayName("LoginRequest - Should create with valid email and password")
  void testLoginRequestValid() {
    LoginRequest request = new LoginRequest("john@example.com", "SecurePass123!");

    assertEquals("john@example.com", request.getEmail());
    assertEquals("SecurePass123!", request.getPassword());
  }

  @Test
  @DisplayName("LoginRequest - Should handle no-arg constructor")
  void testLoginRequestNoArgConstructor() {
    LoginRequest request = new LoginRequest();
    assertNotNull(request);
    assertNull(request.getEmail());
    assertNull(request.getPassword());
  }

  @Test
  @DisplayName("LoginRequest - Should handle all-arg constructor")
  void testLoginRequestAllArgConstructor() {
    LoginRequest request = new LoginRequest("user@example.com", "Password123");

    assertEquals("user@example.com", request.getEmail());
    assertEquals("Password123", request.getPassword());
  }

  @Test
  @DisplayName("LoginRequest - Should handle getters and setters for email")
  void testLoginRequestEmailGetterSetter() {
    LoginRequest request = new LoginRequest();
    request.setEmail("test@example.com");

    assertEquals("test@example.com", request.getEmail());
  }

  @Test
  @DisplayName("LoginRequest - Should handle getters and setters for password")
  void testLoginRequestPasswordGetterSetter() {
    LoginRequest request = new LoginRequest();
    request.setPassword("TestPassword123");

    assertEquals("TestPassword123", request.getPassword());
  }

  @Test
  @DisplayName("LoginRequest - Should allow multiple updates")
  void testLoginRequestMultipleUpdates() {
    LoginRequest request = new LoginRequest("initial@example.com", "InitialPass123");

    assertEquals("initial@example.com", request.getEmail());
    assertEquals("InitialPass123", request.getPassword());

    request.setEmail("updated@example.com");
    request.setPassword("UpdatedPass123");

    assertEquals("updated@example.com", request.getEmail());
    assertEquals("UpdatedPass123", request.getPassword());
  }
}
