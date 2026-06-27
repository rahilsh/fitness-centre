package com.rsh.fitness_centre.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rsh.fitness_centre.entity.User;
import com.rsh.fitness_centre.entity.request.AddUserRequest;
import com.rsh.fitness_centre.service.UserService;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
class UserControllerTest {

  private UserController userController;

  @Mock
  private UserService userService;

  @BeforeEach
  void setUp() {
    userController = new UserController(userService);
  }

  @Test
  @DisplayName("Should add user successfully")
  void testAddUserSuccess() {
    // Arrange
    AddUserRequest request = new AddUserRequest();
    request.setName("John Doe");
    User user = new User(1, "John Doe");
    when(userService.addUser("John Doe")).thenReturn(user);

    // Act
    User result = userController.addUser(request);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals("John Doe", result.getName());
    verify(userService, times(1)).addUser("John Doe");
  }

  @Test
  @DisplayName("Should get all users successfully")
  void testGetAllUsersSuccess() {
    // Arrange
    Set<User> users = new HashSet<>();
    users.add(new User(1, "John Doe"));
    users.add(new User(2, "Jane Doe"));
    when(userService.getAllUsers()).thenReturn(users);

    // Act
    Set<User> result = userController.addUser();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    verify(userService, times(1)).getAllUsers();
  }

  @Test
  @DisplayName("Should add user with empty name")
  void testAddUserWithEmptyName() {
    // Arrange
    AddUserRequest request = new AddUserRequest();
    request.setName("");
    User user = new User(2, "");
    when(userService.addUser("")).thenReturn(user);

    // Act
    User result = userController.addUser(request);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getId());
    assertEquals("", result.getName());
  }

  @Test
  @DisplayName("Should get empty set of users")
  void testGetAllUsersEmpty() {
    // Arrange
    when(userService.getAllUsers()).thenReturn(new HashSet<>());

    // Act
    Set<User> result = userController.addUser();

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  @DisplayName("Should add user with special characters")
  void testAddUserWithSpecialCharacters() {
    // Arrange
    AddUserRequest request = new AddUserRequest();
    request.setName("John@#$%Doe");
    User user = new User(3, "John@#$%Doe");
    when(userService.addUser("John@#$%Doe")).thenReturn(user);

    // Act
    User result = userController.addUser(request);

    // Assert
    assertEquals("John@#$%Doe", result.getName());
  }

  @Test
  @DisplayName("Should add user with long name")
  void testAddUserWithLongName() {
    // Arrange
    String longName = "A".repeat(500);
    AddUserRequest request = new AddUserRequest();
    request.setName(longName);
    User user = new User(4, longName);
    when(userService.addUser(longName)).thenReturn(user);

    // Act
    User result = userController.addUser(request);

    // Assert
    assertEquals(4, result.getId());
    assertEquals(longName, result.getName());
  }

  @Test
  @DisplayName("Should handle multiple add user requests")
  void testAddMultipleUsers() {
    // Arrange
    AddUserRequest request1 = new AddUserRequest();
    request1.setName("User1");
    AddUserRequest request2 = new AddUserRequest();
    request2.setName("User2");

    User user1 = new User(1, "User1");
    User user2 = new User(2, "User2");

    when(userService.addUser("User1")).thenReturn(user1);
    when(userService.addUser("User2")).thenReturn(user2);

    // Act
    User result1 = userController.addUser(request1);
    User result2 = userController.addUser(request2);

    // Assert
    assertEquals(1, result1.getId());
    assertEquals(2, result2.getId());
    verify(userService, times(1)).addUser("User1");
    verify(userService, times(1)).addUser("User2");
  }

  @Test
  @DisplayName("Should verify controller delegates to service")
  void testControllerDelegatesToService() {
    // Arrange
    AddUserRequest request = new AddUserRequest();
    request.setName("Test User");
    User user = new User(5, "Test User");
    when(userService.addUser("Test User")).thenReturn(user);

    // Act
    User result = userController.addUser(request);

    // Assert
    assertNotNull(result);
    verify(userService, times(1)).addUser("Test User");
  }
}
